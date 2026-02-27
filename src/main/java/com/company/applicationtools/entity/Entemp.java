package com.company.applicationtools.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ENTEMP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entemp {

    @EmbeddedId
    private EntempId id;

    @Column(name = "NAME", length = 35)
    private String name;

    @Column(name = "GRADE")
    private Integer grade;

    @Column(name = "TYPE", length = 1)
    private String type;

    @Column(name = "ICSACC", length = 1)
    private String icsacc;

    @Column(name = "BADGE", length = 10)
    private String badge;

    @Column(name = "TITLE", length = 25)
    private String title;

    @Column(name = "AREACD")
    private Integer areacd;

    @Column(name = "PHONE")
    private Integer phone;

    @Column(name = "EXT")
    private Integer ext;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "POSTYPE", length = 1)
    private String postype;

    @Column(name = "AREA", length = 1)
    private String area;

    @Column(name = "TOUR")
    private Integer tour;

    @Column(name = "PODIND", length = 1)
    private String podind;

    @Column(name = "TPSIND", length = 1)
    private String tpsind;

    @Column(name = "CSUIND", length = 1)
    private String csuind;

    @Column(name = "AIDEIND", length = 1)
    private String aideind;

    @Column(name = "FLEXIND", length = 1)
    private String flexind;

    @Column(name = "EMPDT")
    private LocalDate empdt;

    @Column(name = "ADJDT")
    private LocalDate adjdt;

    @Column(name = "ADJREASON", length = 4)
    private String adjreason;

    @Column(name = "ADJPERCENT")
    private Integer adjpercent;

    @Column(name = "PREVID")
    private Long previd;

    @Column(name = "EACTIVE", length = 1)
    private String eactive;

    @Column(name = "UNIX", length = 8)
    private String unix;

    @Column(name = "ELEVEL")
    private Integer elevel;

    @Column(name = "EXTRDT")
    private LocalDate extrdt;

    @Column(name = "PRIMARY_ROID", length = 1)
    private String primaryRoid;

    @Column(name = "PODCD", length = 3)
    private String podcd;

    @Column(name = "ORG", length = 2)
    private String org;

    @Column(name = "LASTLOGIN")
    private LocalDate lastlogin;

    @Column(name = "GS9CNT")
    private Integer gs9cnt;

    @Column(name = "GS11CNT")
    private Integer gs11cnt;

    @Column(name = "GS12CNT")
    private Integer gs12cnt;

    @Column(name = "GS13CNT")
    private Integer gs13cnt;

    @Column(name = "LOGOFF")
    private LocalDate logoff;

    @Column(name = "IP_ADDR", length = 39)
    private String ipAddr;

    // =========================================================================
    // Helper Methods
    // =========================================================================

    public String getTrimmedName() {
        return name != null ? name.trim() : null;
    }

    public String getFirstName() {
        String trimmed = getTrimmedName();
        if (trimmed == null) return null;
        int spaceIdx = trimmed.indexOf(' ');
        return spaceIdx > 0 ? trimmed.substring(0, spaceIdx) : trimmed;
    }

    public String getLastName() {
        String trimmed = getTrimmedName();
        if (trimmed == null) return null;
        int spaceIdx = trimmed.indexOf(' ');
        return spaceIdx > 0 ? trimmed.substring(spaceIdx + 1).trim() : "";
    }

    public String getFormattedPhone() {
        if (areacd == null && phone == null) return null;
        String areaStr = areacd != null ? String.valueOf(areacd) : "000";
        String phoneStr = phone != null ? String.format("%07d", phone) : "0000000";
        return String.format("(%s)%s-%s", areaStr, phoneStr.substring(0, 3), phoneStr.substring(3));
    }
}
