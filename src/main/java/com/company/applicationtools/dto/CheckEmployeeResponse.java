package com.company.applicationtools.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmployeeResponse {

    private Long assignmentNumber;     // ROID
    private String unixLogin;          // UNIX
    private String seid;               // SEID
    private Integer level;             // ELEVEL
    private String active;             // EACTIVE
    private Integer tour;              // TOUR
    private String positionType;       // POSTYPE
    private String org;                // ORG
    private String employeePhone;      // AREACD + PHONE formatted
    private String employeeName;       // NAME
    private String title;              // TITLE
    private String primaryId;          // PRIMARY_ROID
    private LocalDate lastAccessDate;  // ENTITY_USER.LOGIN_DATE
    private String icsPositionType;    // POSTYPE (same as positionType)
}
