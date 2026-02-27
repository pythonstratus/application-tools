package com.company.applicationtools.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckEmployeeSearchRequest {

    @NotBlank(message = "Field is required")
    private String field;

    @NotBlank(message = "Operator is required")
    private String operator;

    private String value;

    private String valueTo;  // For BETWEEN operator
}
