package com.career.careerlink.employers.entity;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import com.career.careerlink.users.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employer_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  String employerUserId;

    @Column(name = "employer_id")
    private String employerId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "role")
    private String role;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "is_approved")
    private AgreementStatus isApproved;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "employer_status")
    private UserStatus employerStatus;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.employerStatus == null) {
            this.employerStatus = UserStatus.ACTIVE;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
