package com.career.careerlink.users.dto;

import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.users.entity.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String userid;
    private String loginId;
    @JsonProperty("password")
    private String passwordHash;
    private String socialType;
    private String socialLoingId;
    private String userName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String gender;
    private String userType;
    private String email;
    private LocalDateTime lastLoginAt;
    private LocalDateTime dormantAt;
    private YnType agreeTerms;
    private YnType agreePrivacy;
    private YnType agreeMarketing;
    private UserStatus userStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
