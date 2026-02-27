# Application Tools API Documentation

## Base URL
```
http://localhost:8081/api/application-tools
```

## Swagger UI
```
http://localhost:8081/api/application-tools/swagger-ui.html
```

---

## Check Employee API (Read-Only)

Base: `/api/v1/check-employee`

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/search` | Dynamic search with field/operator/value |
| GET | `/fields` | Searchable fields for dropdown |
| GET | `/operators` | Available operators for dropdown |

### Search (POST /search)
**Request:**
```json
{
  "field": "EMPLOYEE_NAME",
  "operator": "LIKE",
  "value": "Shannon"
}
```

**For BETWEEN operator:**
```json
{
  "field": "ASSIGNMENT_NUM",
  "operator": "BETWEEN",
  "value": "10000000",
  "valueTo": "30000000"
}
```

**For IN operator (comma-separated):**
```json
{
  "field": "SEID",
  "operator": "IN",
  "value": "12345,12346,123JB"
}
```

**For EMPTY/NOT_EMPTY (no value needed):**
```json
{
  "field": "EMPLOYEE_NAME",
  "operator": "NOT_EMPTY"
}
```

**Response:**
```json
[
  {
    "assignmentNumber": 25011414,
    "unixLogin": "sademe75",
    "seid": "12345",
    "level": 8,
    "active": "Y",
    "tour": 1,
    "positionType": "1",
    "org": "CF",
    "employeePhone": "(214)413-3333",
    "employeeName": "Shannon A Demer",
    "title": "Revenue Officer",
    "primaryId": "N",
    "lastAccessDate": "2025-05-23",
    "icsPositionType": "1"
  }
]
```

### Available Fields (GET /fields)
| Value | Label |
|-------|-------|
| ASSIGNMENT_NUM | Assignment # |
| SUBSTR_ROID | Substr(roid, 1, 6) |
| SEID | SEID |
| EMPLOYEE_NAME | Employee Name |

### Available Operators (GET /operators)
`=`, `>`, `<`, `>=`, `<=`, `LIKE`, `NOT_LIKE`, `IN`, `NOT_IN`, `BETWEEN`, `NOT_BETWEEN`, `EMPTY`, `NOT_EMPTY`

---

## National User Options API (Full CRUD)

Base: `/api/v1/national-users`

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/search` | Search users by any combination of fields |
| GET | `/{roid}/{seid}` | Get user by composite key |
| POST | `/` | Add new user |
| PUT | `/{roid}/{seid}` | Modify user (all fields editable) |
| DELETE | `/{roid}/{seid}` | Delete user |
| GET | `/orgs` | Distinct ORG values for dropdown |

### Search Users (POST /search)
All fields optional:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": null,
  "userGrade": 13,
  "seid": null,
  "telephone": null,
  "userOrg": "CF"
}
```

**Response:**
```json
[
  {
    "roid": 10001001,
    "seid": "123JB",
    "firstName": "John",
    "lastName": "Doe",
    "userGrade": 13,
    "email": "john.doe@test.com",
    "telephone": "(101)234-5678",
    "userOrg": "CF"
  }
]
```

### Add User (POST /)
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@test.com",
  "userGrade": 12,
  "seid": "JS001",
  "telephone": "(202)555-1234",
  "userOrg": "AD"
}
```

### Modify User (PUT /{roid}/{seid})
```json
{
  "firstName": "Jane",
  "lastName": "Smith-Updated",
  "email": "jane.updated@test.com",
  "userGrade": 13,
  "seid": "JS001",
  "telephone": "(202)555-9999",
  "userOrg": "WI"
}
```

### Delete User (DELETE /{roid}/{seid})
Response: 204 No Content

### Get Orgs (GET /orgs)
```json
["AD", "CF", "CP", "WI"]
```

---

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2026-02-27T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "National User not found with ROID: 999, SEID: XXXXX"
}
```

### 400 Bad Request
```json
{
  "timestamp": "2026-02-27T14:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid request data",
  "validationErrors": {
    "firstName": "First name is required"
  }
}
```

---

## Tables Used

| Table | Usage | Access |
|-------|-------|--------|
| ENTEMP | Employee data (39 columns) | Read + Write |
| ENTITY_USER | Login tracking (12 columns) | Read-only (LEFT JOIN for Last Access Date) |

## Quick Start
```bash
# Run with H2 (local dev)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run with Oracle
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build
mvn clean package -DskipTests
```

## H2 Console (local profile)
```
URL: http://localhost:8081/api/application-tools/h2-console
JDBC URL: jdbc:h2:mem:apptools
Username: sa
Password: (empty)
```
