package com.company.applicationtools.service;

import com.company.applicationtools.dto.CheckEmployeeResponse;
import com.company.applicationtools.dto.CheckEmployeeSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckEmployeeService {

    @PersistenceContext
    private EntityManager entityManager;

    // Searchable fields: display label -> actual column expression
    private static final Map<String, String> FIELD_MAP = new LinkedHashMap<>();
    static {
        FIELD_MAP.put("ASSIGNMENT_NUM", "e.ROID");
        FIELD_MAP.put("SUBSTR_ROID", "CAST(e.ROID AS VARCHAR(20))");
        FIELD_MAP.put("SEID", "e.SEID");
        FIELD_MAP.put("EMPLOYEE_NAME", "e.NAME");
    }

    // Valid operators
    private static final Set<String> VALID_OPERATORS = Set.of(
            "=", ">", "<", ">=", "<=",
            "LIKE", "NOT_LIKE", "IN", "NOT_IN",
            "BETWEEN", "NOT_BETWEEN", "EMPTY", "NOT_EMPTY"
    );

    // =========================================================================
    // Search - Dynamic Query Builder
    // =========================================================================

    public List<CheckEmployeeResponse> search(CheckEmployeeSearchRequest request) {
        log.info("Check Employee search: field={}, operator={}, value={}",
                request.getField(), request.getOperator(), request.getValue());

        String fieldKey = request.getField().toUpperCase();
        String operator = request.getOperator().toUpperCase();

        if (!FIELD_MAP.containsKey(fieldKey)) {
            throw new IllegalArgumentException("Invalid field: " + request.getField()
                    + ". Valid fields: " + FIELD_MAP.keySet());
        }
        if (!VALID_OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Invalid operator: " + request.getOperator()
                    + ". Valid operators: " + VALID_OPERATORS);
        }

        String columnExpr = FIELD_MAP.get(fieldKey);

        // Build the WHERE clause
        StringBuilder whereClause = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        buildWhereClause(whereClause, params, columnExpr, operator, request.getValue(), request.getValueTo());

        // Build full query with LEFT JOIN to ENTITY_USER for Last Access Date
        String sql = """
            SELECT
                e.ROID,
                e.UNIX,
                e.SEID,
                e.ELEVEL,
                e.EACTIVE,
                e.TOUR,
                e.POSTYPE,
                e.ORG,
                e.AREACD,
                e.PHONE,
                e.NAME,
                e.TITLE,
                e.PRIMARY_ROID,
                eu.LOGIN_DATE
            FROM ENTEMP e
            LEFT JOIN ENTITY_USER eu ON TRIM(e.SEID) = TRIM(eu.USER_SEID)
            WHERE """ + whereClause.toString() + """
            
            ORDER BY e.ROID
            """;

        log.debug("Executing SQL: {}", sql);

        Query query = entityManager.createNativeQuery(sql);
        params.forEach(query::setParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToCheckEmployeeResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Lookup Data
    // =========================================================================

    public List<Map<String, String>> getSearchableFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(Map.of("value", "ASSIGNMENT_NUM", "label", "Assignment #"));
        fields.add(Map.of("value", "SUBSTR_ROID", "label", "Substr(roid, 1, 6)"));
        fields.add(Map.of("value", "SEID", "label", "SEID"));
        fields.add(Map.of("value", "EMPLOYEE_NAME", "label", "Employee Name"));
        return fields;
    }

    public List<Map<String, String>> getOperators() {
        List<Map<String, String>> operators = new ArrayList<>();
        operators.add(Map.of("value", "=", "label", "="));
        operators.add(Map.of("value", ">", "label", ">"));
        operators.add(Map.of("value", "<", "label", "<"));
        operators.add(Map.of("value", ">=", "label", ">="));
        operators.add(Map.of("value", "<=", "label", "<="));
        operators.add(Map.of("value", "LIKE", "label", "Like"));
        operators.add(Map.of("value", "NOT_LIKE", "label", "Not Like"));
        operators.add(Map.of("value", "IN", "label", "In"));
        operators.add(Map.of("value", "NOT_IN", "label", "Not In"));
        operators.add(Map.of("value", "BETWEEN", "label", "Between"));
        operators.add(Map.of("value", "NOT_BETWEEN", "label", "Not Between"));
        operators.add(Map.of("value", "EMPTY", "label", "Empty"));
        operators.add(Map.of("value", "NOT_EMPTY", "label", "Not Empty"));
        return operators;
    }

    // =========================================================================
    // WHERE Clause Builder
    // =========================================================================

    private void buildWhereClause(StringBuilder where, Map<String, Object> params,
                                   String column, String operator, String value, String valueTo) {

        switch (operator) {
            case "=" -> {
                where.append(column).append(" = :val");
                params.put("val", parseValue(value));
            }
            case ">" -> {
                where.append(column).append(" > :val");
                params.put("val", parseValue(value));
            }
            case "<" -> {
                where.append(column).append(" < :val");
                params.put("val", parseValue(value));
            }
            case ">=" -> {
                where.append(column).append(" >= :val");
                params.put("val", parseValue(value));
            }
            case "<=" -> {
                where.append(column).append(" <= :val");
                params.put("val", parseValue(value));
            }
            case "LIKE" -> {
                where.append("UPPER(").append(column).append(") LIKE UPPER(:val)");
                params.put("val", "%" + value + "%");
            }
            case "NOT_LIKE" -> {
                where.append("UPPER(").append(column).append(") NOT LIKE UPPER(:val)");
                params.put("val", "%" + value + "%");
            }
            case "IN" -> {
                String[] values = value.split(",");
                where.append(column).append(" IN (");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) where.append(", ");
                    String paramName = "val" + i;
                    where.append(":").append(paramName);
                    params.put(paramName, parseValue(values[i].trim()));
                }
                where.append(")");
            }
            case "NOT_IN" -> {
                String[] values2 = value.split(",");
                where.append(column).append(" NOT IN (");
                for (int i = 0; i < values2.length; i++) {
                    if (i > 0) where.append(", ");
                    String paramName = "val" + i;
                    where.append(":").append(paramName);
                    params.put(paramName, parseValue(values2[i].trim()));
                }
                where.append(")");
            }
            case "BETWEEN" -> {
                where.append(column).append(" BETWEEN :valFrom AND :valTo");
                params.put("valFrom", parseValue(value));
                params.put("valTo", parseValue(valueTo));
            }
            case "NOT_BETWEEN" -> {
                where.append(column).append(" NOT BETWEEN :valFrom AND :valTo");
                params.put("valFrom", parseValue(value));
                params.put("valTo", parseValue(valueTo));
            }
            case "EMPTY" -> {
                where.append("(").append(column).append(" IS NULL OR TRIM(")
                     .append(column).append(") = '')");
            }
            case "NOT_EMPTY" -> {
                where.append(column).append(" IS NOT NULL AND TRIM(")
                     .append(column).append(") != ''");
            }
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    private Object parseValue(String value) {
        if (value == null) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return value.trim();
        }
    }

    // =========================================================================
    // Result Mapping
    // =========================================================================

    private CheckEmployeeResponse mapToCheckEmployeeResponse(Object[] row) {
        return CheckEmployeeResponse.builder()
                .assignmentNumber(toLong(row[0]))
                .unixLogin(toString(row[1]))
                .seid(toString(row[2]))
                .level(toInt(row[3]))
                .active(toString(row[4]))
                .tour(toInt(row[5]))
                .positionType(toString(row[6]))
                .org(toString(row[7]))
                .employeePhone(formatPhone(row[8], row[9]))
                .employeeName(toString(row[10]))
                .title(toString(row[11]))
                .primaryId(toString(row[12]))
                .lastAccessDate(toLocalDate(row[13]))
                .icsPositionType(toString(row[6]))  // Same as positionType (POSTYPE)
                .build();
    }

    // =========================================================================
    // Formatting Helpers
    // =========================================================================

    private String formatPhone(Object areacd, Object phone) {
        if (areacd == null && phone == null) return null;
        String areaStr = areacd != null ? String.valueOf(toInt(areacd)) : "000";
        String phoneStr = phone != null ? String.format("%07d", toInt(phone)) : "0000000";
        return String.format("(%s)%s-%s", areaStr, phoneStr.substring(0, 3), phoneStr.substring(3));
    }

    // =========================================================================
    // Type Conversion Helpers
    // =========================================================================

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private String toString(Object value) {
        if (value == null) return null;
        return value.toString().trim();
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }
}
