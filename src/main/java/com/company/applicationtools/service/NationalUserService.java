package com.company.applicationtools.service;

import com.company.applicationtools.dto.*;
import com.company.applicationtools.entity.Entemp;
import com.company.applicationtools.entity.EntempId;
import com.company.applicationtools.exception.ResourceNotFoundException;
import com.company.applicationtools.repository.EntempRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NationalUserService {

    private final EntempRepository entempRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // =========================================================================
    // Search (Multi-field, all optional)
    // =========================================================================

    public List<NationalUserResponse> search(NationalUserSearchRequest request) {
        log.info("National User search: {}", request);

        StringBuilder sql = new StringBuilder("""
            SELECT e.ROID, e.SEID, e.NAME, e.GRADE, e.EMAIL, e.AREACD, e.PHONE, e.ORG
            FROM ENTEMP e
            WHERE 1=1
            """);

        Map<String, Object> params = new HashMap<>();

        if (hasValue(request.getFirstName())) {
            sql.append(" AND UPPER(e.NAME) LIKE UPPER(:firstName)");
            params.put("firstName", request.getFirstName().trim() + "%");
        }

        if (hasValue(request.getLastName())) {
            sql.append(" AND UPPER(e.NAME) LIKE UPPER(:lastName)");
            params.put("lastName", "% " + request.getLastName().trim() + "%");
        }

        if (hasValue(request.getEmail())) {
            sql.append(" AND UPPER(e.EMAIL) LIKE UPPER(:email)");
            params.put("email", "%" + request.getEmail().trim() + "%");
        }

        if (request.getUserGrade() != null) {
            sql.append(" AND e.GRADE = :grade");
            params.put("grade", request.getUserGrade());
        }

        if (hasValue(request.getSeid())) {
            sql.append(" AND TRIM(e.SEID) = :seid");
            params.put("seid", request.getSeid().trim());
        }

        if (hasValue(request.getTelephone())) {
            // Search across combined area code + phone
            String digits = request.getTelephone().replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                String area = digits.substring(0, 3);
                String phone = digits.substring(3, 10);
                sql.append(" AND e.AREACD = :areacd AND e.PHONE = :phone");
                params.put("areacd", Integer.parseInt(area));
                params.put("phone", Integer.parseInt(phone));
            } else if (digits.length() >= 7) {
                sql.append(" AND e.PHONE = :phone");
                params.put("phone", Integer.parseInt(digits.substring(0, 7)));
            }
        }

        if (hasValue(request.getUserOrg())) {
            sql.append(" AND TRIM(e.ORG) = :org");
            params.put("org", request.getUserOrg().trim());
        }

        sql.append(" ORDER BY e.NAME");

        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToNationalUserResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Get By ID
    // =========================================================================

    public NationalUserResponse getById(Long roid, String seid) {
        log.debug("Fetching national user: roid={}, seid={}", roid, seid);

        EntempId id = new EntempId(roid, seid);
        Entemp entity = entempRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "National User not found with ROID: " + roid + ", SEID: " + seid));

        return mapEntityToResponse(entity);
    }

    // =========================================================================
    // Add User
    // =========================================================================

    @Transactional
    public NationalUserResponse addUser(NationalUserRequest request) {
        log.info("Adding national user: {} {}", request.getFirstName(), request.getLastName());

        String combinedName = request.getFirstName().trim() + " " + request.getLastName().trim();

        // Parse phone into area code and phone number
        Integer areacd = null;
        Integer phone = null;
        if (hasValue(request.getTelephone())) {
            String digits = request.getTelephone().replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                areacd = Integer.parseInt(digits.substring(0, 3));
                phone = Integer.parseInt(digits.substring(3, 10));
            } else if (digits.length() >= 7) {
                phone = Integer.parseInt(digits.substring(0, 7));
            }
        }

        // Generate ROID - get max + 1
        Query maxQuery = entityManager.createNativeQuery("SELECT COALESCE(MAX(ROID), 0) + 1 FROM ENTEMP");
        Long newRoid = ((Number) maxQuery.getSingleResult()).longValue();

        Entemp entity = Entemp.builder()
                .id(new EntempId(newRoid, request.getSeid()))
                .name(combinedName)
                .grade(request.getUserGrade())
                .email(request.getEmail())
                .areacd(areacd)
                .phone(phone)
                .org(request.getUserOrg())
                .eactive("Y")
                .build();

        Entemp saved = entempRepository.save(entity);
        log.info("National user added: ROID={}, SEID={}", saved.getId().getRoid(), saved.getId().getSeid());

        return mapEntityToResponse(saved);
    }

    // =========================================================================
    // Modify User
    // =========================================================================

    @Transactional
    public NationalUserResponse modifyUser(Long roid, String seid, NationalUserRequest request) {
        log.info("Modifying national user: roid={}, seid={}", roid, seid);

        EntempId id = new EntempId(roid, seid);
        Entemp entity = entempRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "National User not found with ROID: " + roid + ", SEID: " + seid));

        // Update name
        if (hasValue(request.getFirstName()) && hasValue(request.getLastName())) {
            entity.setName(request.getFirstName().trim() + " " + request.getLastName().trim());
        } else if (hasValue(request.getFirstName())) {
            entity.setName(request.getFirstName().trim() + " " + entity.getLastName());
        } else if (hasValue(request.getLastName())) {
            entity.setName(entity.getFirstName() + " " + request.getLastName().trim());
        }

        if (request.getUserGrade() != null) {
            entity.setGrade(request.getUserGrade());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getUserOrg() != null) {
            entity.setOrg(request.getUserOrg());
        }
        if (request.getSeid() != null) {
            // SEID is part of composite key - for simplicity we update non-key fields only
            // If SEID changes, that requires delete + re-insert
            log.warn("SEID is part of composite key and cannot be updated in-place");
        }

        // Update phone
        if (hasValue(request.getTelephone())) {
            String digits = request.getTelephone().replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                entity.setAreacd(Integer.parseInt(digits.substring(0, 3)));
                entity.setPhone(Integer.parseInt(digits.substring(3, 10)));
            } else if (digits.length() >= 7) {
                entity.setPhone(Integer.parseInt(digits.substring(0, 7)));
            }
        }

        Entemp saved = entempRepository.save(entity);
        return mapEntityToResponse(saved);
    }

    // =========================================================================
    // Delete User
    // =========================================================================

    @Transactional
    public void deleteUser(Long roid, String seid) {
        log.info("Deleting national user: roid={}, seid={}", roid, seid);

        EntempId id = new EntempId(roid, seid);
        if (!entempRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "National User not found with ROID: " + roid + ", SEID: " + seid);
        }

        entempRepository.deleteById(id);
        log.info("National user deleted: roid={}, seid={}", roid, seid);
    }

    // =========================================================================
    // Org Dropdown
    // =========================================================================

    public List<String> getDistinctOrgs() {
        return entempRepository.findDistinctOrgs();
    }

    // =========================================================================
    // Mapping - Native Query Result
    // =========================================================================

    private NationalUserResponse mapToNationalUserResponse(Object[] row) {
        String fullName = toString(row[2]);
        String firstName = null;
        String lastName = null;
        if (fullName != null) {
            int spaceIdx = fullName.indexOf(' ');
            if (spaceIdx > 0) {
                firstName = fullName.substring(0, spaceIdx);
                lastName = fullName.substring(spaceIdx + 1).trim();
            } else {
                firstName = fullName;
                lastName = "";
            }
        }

        return NationalUserResponse.builder()
                .roid(toLong(row[0]))
                .seid(toString(row[1]))
                .firstName(firstName)
                .lastName(lastName)
                .userGrade(toInt(row[3]))
                .email(toString(row[4]))
                .telephone(formatPhone(row[5], row[6]))
                .userOrg(toString(row[7]))
                .build();
    }

    // Mapping - Entity
    private NationalUserResponse mapEntityToResponse(Entemp entity) {
        return NationalUserResponse.builder()
                .roid(entity.getId().getRoid())
                .seid(entity.getId().getSeid() != null ? entity.getId().getSeid().trim() : null)
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .userGrade(entity.getGrade())
                .email(entity.getEmail() != null ? entity.getEmail().trim() : null)
                .telephone(entity.getFormattedPhone())
                .userOrg(entity.getOrg() != null ? entity.getOrg().trim() : null)
                .build();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }

    private String formatPhone(Object areacd, Object phone) {
        if (areacd == null && phone == null) return null;
        String areaStr = areacd != null ? String.valueOf(toInt(areacd)) : "000";
        String phoneStr = phone != null ? String.format("%07d", toInt(phone)) : "0000000";
        return String.format("(%s)%s-%s", areaStr, phoneStr.substring(0, 3), phoneStr.substring(3));
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private String toString(Object value) {
        if (value == null) return null;
        return value.toString().trim();
    }
}
