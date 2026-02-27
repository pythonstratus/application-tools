package com.company.applicationtools.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ENTITY_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityUser {

    @Id
    @Column(name = "ENTITY_USER_ID")
    private Long entityUserId;

    @Column(name = "USER_SEID", length = 50)
    private String userSeid;

    @Column(name = "USER_NAME", length = 50)
    private String userName;

    @Column(name = "LOGIN_DATE")
    private LocalDate loginDate;

    @Column(name = "CREATE_DATE")
    private LocalDate createDate;

    @Column(name = "CREATE_USER", length = 30)
    private String createUser;

    @Column(name = "UPDATE_DATE")
    private LocalDate updateDate;

    @Column(name = "UPDATE_USER", length = 30)
    private String updateUser;

    @Column(name = "ISSTAFF", length = 5)
    private String isStaff;

    @Column(name = "ISLOCKED", length = 5)
    private String isLocked;

    @Column(name = "PASSWORD", length = 10)
    private String password;

    @Column(name = "ORG", length = 2)
    private String org;
}
