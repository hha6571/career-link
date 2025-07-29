package com.career.careerlink.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import com.career.careerlink.users.entity.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "social_type")
    private String socialType;

    @Column(name = "social_login_id")
    private String socialLoingId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "dormant_at")
    private LocalDateTime dormantAt;

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

    @Column(name = "created_at")
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
