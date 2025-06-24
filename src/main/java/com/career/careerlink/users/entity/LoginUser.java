package com.career.careerlink.users.entity;

import com.career.careerlink.users.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "v_user_login_info")
@Getter
public class LoginUser {

    @Id
    @Column(name = "login_id")
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "user_pk")
    private String userPk;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;

    @Column(name = "dormant_at")
    private LocalDateTime dormantAt;
}
