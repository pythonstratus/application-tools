package com.company.applicationtools.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntempId implements Serializable {

    @Column(name = "ROID")
    private Long roid;

    @Column(name = "SEID", length = 5)
    private String seid;
}
