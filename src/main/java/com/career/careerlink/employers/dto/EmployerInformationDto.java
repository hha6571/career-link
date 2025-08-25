package com.career.careerlink.employers.dto;

import com.career.careerlink.employers.entity.Employer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployerInformationDto {
    private String employerId;
    private String companyTypeCode;
    private String companyName;
    private String bizRegNo;
    private String bizRegistrationUrl;
    private String ceoName;
    private String companyPhone;
    private String companyEmail;
    private String baseAddress;
    private String detailAddress;
    private String postcode;
    private LocalDate establishedDate;
    private String industryCode;
    private String companyIntro;
    private String homepageUrl;
    private String companyLogoUrl;
    private int employeeCount;

    public EmployerInformationDto(Employer e) {
        this.employerId = e.getEmployerId();
        this.companyTypeCode = e.getCompanyTypeCode();
        this.companyName = e.getCompanyName();
        this.bizRegNo = e.getBizRegNo();
        this.ceoName = e.getCeoName();
        this.companyPhone = e.getCompanyPhone();
        this.companyEmail = e.getCompanyEmail();
        this.baseAddress = e.getBaseAddress();
        this.detailAddress = e.getDetailAddress();
        this.postcode = e.getPostcode();
        this.establishedDate = e.getEstablishedDate();
        this.industryCode = e.getIndustryCode();
        this.companyIntro = e.getCompanyIntro();
        this.homepageUrl = e.getHomepageUrl();
        this.companyLogoUrl = e.getCompanyLogoUrl();
        this.employeeCount = e.getEmployeeCount();
    }
}
