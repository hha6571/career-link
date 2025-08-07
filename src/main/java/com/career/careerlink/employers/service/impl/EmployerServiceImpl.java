package com.career.careerlink.employers.service.impl;

import com.career.careerlink.employers.dto.EmployerInfomationDto;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.entity.Employer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final S3Service s3Service;

    @Override
    public boolean isCompanyDuplicate(String bizRegNo) {
        return employerRepository.existsByBizRegNo(bizRegNo);
    }

    @Override
    public void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file) {
        String generatedUserId = UserIdGenerator.generate("EMP");
        String url = s3Service.uploadFile(S3UploadType.BUSINESS_CERTIFICATE, file);

        Employer newEmployers = Employer.builder()
                .employerId(generatedUserId)
                .companyName(dto.getCompanyName())
                .bizRegNo(dto.getBizRegNo())
                .bizRegistrationUrl(url)
                .ceoName(dto.getCeoName())
                .companyEmail(dto.getCompanyEmail())
                .establishedDate(dto.getEstablishedDate())
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
                .ceoName(employer.getCeoName())
                .companyPhone(employer.getCompanyPhone())
                .companyEmail(employer.getCompanyEmail())
                .baseAddress(employer.getBaseAddress())
                .detailAddress(employer.getDetailAddress())
                .postcode(employer.getPostcode())
                .establishedDate(employer.getEstablishedDate())
                .industryCode(employer.getIndustryCode())
                .companyIntro(employer.getCompanyIntro())
                .homepageUrl(employer.getHomepageUrl())
                .companyLogoUrl(employer.getCompanyLogoUrl())
                .employeeCount(employer.getEmployeeCount())
                .build();
    }


    @Override
    public void saveEmployerInfo(EmployerInfomationDto dto
            //, MultipartFile companyLogo
) {
        Employer employer = new Employer();

        employer.setEmployerId(dto.getEmployerId());
        employer.setCompanyName(dto.getCompanyName());
        employer.setCeoName(dto.getCeoName());
        employer.setBizRegNo(dto.getBizRegNo());
        employer.setCompanyEmail(dto.getCompanyEmail());
        employer.setCompanyPhone(dto.getCompanyPhone());
        employer.setBaseAddress(dto.getBaseAddress());
        employer.setDetailAddress(dto.getDetailAddress());
        employer.setPostcode(dto.getPostcode());
        employer.setIndustryCode(dto.getIndustryCode());
        employer.setCompanyIntro(dto.getCompanyIntro());
        employer.setHomepageUrl(dto.getHomepageUrl());
        employer.setCompanyTypeCode(dto.getCompanyTypeCode());
        employer.setEmployeeCount(dto.getEmployeeCount());
        employer.setEstablishedDate(dto.getEstablishedDate());

//        if (companyLogo != null && !companyLogo.isEmpty()) {
//            String uploadedUrl = fileService.upload(companyLogo);
//            employer.setCompanyLogoUrl(uploadedUrl);
//        }

        employerRepository.save(employer);
    }
}