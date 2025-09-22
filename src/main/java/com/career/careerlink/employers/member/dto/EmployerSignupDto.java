package com.career.careerlink.employers.member.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import com.career.careerlink.users.entity.enums.UserStatus;
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
public class EmployerSignupDto {
    private String employerUserId;
    private String employerId;
    private String employerLoginId;
    private String passwordHash;
    private String userName;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private String role;
    private String deptName;
    private AgreementStatus isApproved;
    private LocalDateTime joinedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime dormantAt;
    private AgreementStatus agreeTerms;
    private AgreementStatus agreePrivacy;
    private AgreementStatus agreeMarketing;
    private UserStatus employerStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
