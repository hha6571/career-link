package com.career.careerlink.employers.entity;

import com.career.careerlink.users.entity.enums.AgreementStatus; // Y/N
import com.career.careerlink.users.entity.enums.UserStatus;      // ACTIVE/DORMANT/WITHDRAWN
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "employer_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employer_login_id", columnNames = "employer_login_id")
        },
        indexes = {
                @Index(name = "idx_employer_id", columnList = "employer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "employerUserId")
public class EmployerUsers {

    @Id
    @Column(name = "employer_user_id", length = 20, nullable = false)
    private String employerUserId;

    @Column(name = "employer_id", length = 20, nullable = false)
    private String employerId;

    @Column(name = "employer_login_id", length = 20, nullable = false)
    private String employerLoginId;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "user_name", length = 20, nullable = false)
    private String userName;

    @Column(name = "phone_number", length = 255, nullable = false)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "role", length = 20, nullable = false)
    @ColumnDefault("'EMPLOYEE'")
    private String role;

    @Column(name = "dept_name", length = 20)
    private String deptName;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_approved", length = 1)
    @ColumnDefault("'N'")
    private AgreementStatus isApproved;

    @Column(name = "approved_at")
    private LocalDateTime joinedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "dormant_at")
    private LocalDateTime dormantAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_terms")
    @ColumnDefault("'Y'")
    private AgreementStatus agreeTerms;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_privacy")
    @ColumnDefault("'Y'")
    private AgreementStatus agreePrivacy;

    @Enumerated(EnumType.STRING)
    @Column(name = "agree_marketing")
    @ColumnDefault("'N'")
    private AgreementStatus agreeMarketing;

    @Enumerated(EnumType.STRING)
    @Column(name = "employer_status")
    @ColumnDefault("'ACTIVE'")
    private UserStatus employerStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.role == null) this.role = "employee";
        if (this.isApproved == null) this.isApproved = AgreementStatus.N;
        if (this.employerStatus == null) this.employerStatus = UserStatus.ACTIVE;
    }
}
