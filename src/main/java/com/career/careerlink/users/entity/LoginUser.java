package com.career.careerlink.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "v_user_login_info")
@Getter
public class LoginUser {

    @Id
    private String loginId;

    private String password;

    private String role;

    private String userPk;

    private String userName;

    private String email;
}
