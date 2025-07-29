package com.career.careerlink.employers.service.impl;

import com.career.careerlink.employers.dto.EmployerInfomationDto;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.entity.Employer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;

    @Override
    public boolean isCompanyDuplicate(String bizRegNo) {
        return employerRepository.existsByBizRegNo(bizRegNo);
    }

    @Override
    public void companyRegistrationRequest(EmployerRegistrationDto dto) {
        String generatedUserId = UserIdGenerator.generate("EMP");

        Employer newEmployers = Employer.builder()
                .employerId(generatedUserId)
                .companyName(dto.getCompanyName())
                .bizRegNo(dto.getBizRegNo())
                .bizRegistrationUrl(dto.getBizRegistrationUrl())
                .companyEmail(dto.getCompanyEmail())
                .isApproved(dto.getIsApproved())
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        employerRepository.save(newEmployers);
    }

    @Override
    public EmployerInfomationDto getCompanyInfomation(String employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 employerId가 존재하지 않습니다: " + employerId));

        return EmployerInfomationDto.builder()
                .employerId(employer.getEmployerId())
                .companyTypeCode(employer.getCompanyTypeCode())
                .companyName(employer.getCompanyName())
                .bizRegNo(employer.getBizRegNo())
                .bizRegistrationUrl(employer.getBizRegistrationUrl())
                .companyEmail(employer.getCompanyEmail())
                .companyPhone(employer.getCompanyPhone())
                .companyEmail(employer.getCompanyEmail())
                .companyAddress(employer.getCompanyAddress())
                .establishedDate(employer.getEstablishedDate())
                .industryCode(employer.getIndustryCode())
                .companyIntro(employer.getCompanyIntro())
                .homepageUrl(employer.getHomepageUrl())
                .companyLogoUrl(employer.getCompanyLogoUrl())
                .employeeCount(employer.getEmployeeCount())
                .build();
    }
}