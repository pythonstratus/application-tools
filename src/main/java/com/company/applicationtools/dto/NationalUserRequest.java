package com.company.applicationtools.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationalUserRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 17, message = "First name too long")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 17, message = "Last name too long")
    private String lastName;

    @Size(max = 50, message = "Email must be less than 50 characters")
    private String email;

    private Integer userGrade;

    @Size(max = 5, message = "SEID must be 5 characters or less")
    private String seid;

    private String telephone;

    @Size(max = 2, message = "User Org must be 2 characters or less")
    private String userOrg;
}
