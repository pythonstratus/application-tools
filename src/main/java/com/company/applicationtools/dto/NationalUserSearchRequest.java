package com.company.applicationtools.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationalUserSearchRequest {

    private String firstName;
    private String lastName;
    private String email;
    private Integer userGrade;
    private String seid;
    private String telephone;
    private String userOrg;
}
