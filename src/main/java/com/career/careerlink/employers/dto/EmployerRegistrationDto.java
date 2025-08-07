package com.career.careerlink.employers.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
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
public class EmployerRegistrationDto {
    private String employerId;
    private String companyName;
    private String bizRegNo;
    private String ceoName;
    private AgreementStatus isApproved;
    private String companyEmail;
    private LocalDate establishedDate;
    private AgreementStatus agreeTerms;
    private AgreementStatus agreePrivacy;
    private AgreementStatus agreeMarketing;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
