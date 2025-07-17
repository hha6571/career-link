package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.enums.AgreementStatus;
import com.career.careerlink.applicant.entity.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate lastLoginAt;
    private LocalDate dormantAt;
    private AgreementStatus agreeTerms;
    private AgreementStatus agreePrivacy;
    private AgreementStatus agreeMarketing;
    private UserStatus userStatus;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
