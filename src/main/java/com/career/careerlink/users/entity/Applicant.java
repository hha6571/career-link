package com.career.careerlink.users.entity;

import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.users.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "ApplicantInfo")
@Table(name = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {
    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "social_type")
    private String socialType;

    @Column(name = "social_login_id")
    private String socialLoginId;

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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "dormant_at")
    private LocalDateTime dormantAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_terms", columnDefinition = "ENUM('Y','N') DEFAULT 'Y'")
    private YnType agreeTerms = YnType.Y;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_privacy", columnDefinition = "ENUM('Y','N') DEFAULT 'Y'")
    private YnType agreePrivacy = YnType.Y;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_marketing", columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private YnType agreeMarketing = YnType.N;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", columnDefinition = "ENUM('ACTIVE','DORMANT','WITHDRAWN') DEFAULT 'ACTIVE'")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @PrePersist
    public void onCreate() {
        if (this.userStatus == null) {
            this.userStatus = UserStatus.ACTIVE;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.userStatus = UserStatus.WITHDRAWN;
        this.updatedAt = LocalDateTime.now();
    }
}
