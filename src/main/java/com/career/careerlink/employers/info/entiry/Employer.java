package com.career.careerlink.employers.info.entiry;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import com.career.careerlink.users.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {
    @Id
    @Column(name = "employer_id")
    private String employerId;

    @Column(name = "company_type_code")
    private String companyTypeCode;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "biz_reg_no", nullable = false)
    private String bizRegNo;

    @Column(name = "biz_registration_url", nullable = false)
    private String bizRegistrationUrl;

    @Column(name = "ceo_name")
    private String ceoName;

    @Column(name = "is_approved", nullable = false)
    @ColumnDefault("'N'")
    @Enumerated(EnumType.STRING)
    private AgreementStatus isApproved;

    @Column(name = "company_phone")
    private String companyPhone;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "base_address")
    private String baseAddress;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(name = "industry_code")
    private String industryCode;

    @Column(name = "company_intro")
    private String companyIntro;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "company_logo_url")
    private String companyLogoUrl;

    @Column(name = "employee_count")
    private int employeeCount;

    @Column(name = "agree_terms")
    @ColumnDefault("'Y'")
    @Enumerated(EnumType.STRING)
    private AgreementStatus agreeTerms;

    @Column(name = "agree_privacy")
    @ColumnDefault("'Y'")
    @Enumerated(EnumType.STRING)
    private AgreementStatus agreePrivacy;

    @Column(name = "agree_marketing")
    @ColumnDefault("'N'")
    @Enumerated(EnumType.STRING)
    private AgreementStatus agreeMarketing;

    @Column(name = "user_status")
    @ColumnDefault("'active'")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.userStatus == null) {
            this.userStatus = UserStatus.ACTIVE;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
