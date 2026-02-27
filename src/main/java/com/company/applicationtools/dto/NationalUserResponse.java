package com.company.applicationtools.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationalUserResponse {

    private Long roid;
    private String seid;
    private String firstName;
    private String lastName;
    private Integer userGrade;
    private String email;
    private String telephone;
    private String userOrg;
}
