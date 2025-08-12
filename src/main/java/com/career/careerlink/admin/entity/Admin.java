package com.career.careerlink.admin.entity;

import com.career.careerlink.users.entity.enums.UserStatus;
import com.career.careerlink.admin.entity.enums.AdminRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @Column(name = "admin_user_id")
    private String adminUserId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @Column(name = "email")
    private String eEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private AdminRole role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "admin_status")
    @Enumerated(EnumType.STRING)
    private UserStatus adminStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.adminStatus == null) {
            this.adminStatus = UserStatus.ACTIVE;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
