package com.career.careerlink.employers.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class EmployerInfomationDto {
    private String employerId;
    private String companyTypeCode;
    private String companyName;
    private String bizRegNo;
    private String bizRegistrationUrl;
    private String companyPhone;
    private String companyEmail;
    private String companyAddress;
    private LocalDate establishedDate;
    private String industryCode;
    private String companyIntro;
    private String homepageUrl;
    private String companyLogoUrl;
    private int employeeCount;
}
