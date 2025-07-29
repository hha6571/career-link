package com.career.careerlink.admin.service.impl;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.repository.AdminRepository;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.admin.spec.EmployerSpecification;
import com.career.careerlink.common.mail.MailService;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest) {
        Specification<Employer> spec = null;

        if (searchRequest.getCompanyName() != null) {
            Specification<Employer> companySpec = EmployerSpecification.hasCompanyNameLike(searchRequest.getCompanyName());
            spec = (spec == null) ? companySpec : spec.and(companySpec);
        }

        if (searchRequest.getIsApproved() != null) {
            Specification<Employer> approvalSpec = EmployerSpecification.hasApprovalStatus(searchRequest.getIsApproved());
            spec = (spec == null) ? approvalSpec : spec.and(approvalSpec);
        }

        List<Employer> employers = (spec == null) ? employerRepository.findAll() : employerRepository.findAll(spec);

        return employers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void approveEmployer(String employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기업이 존재하지 않습니다: " + employerId));

        employer.setIsApproved(AgreementStatus.Y);
        employerRepository.save(employer);

        sendApprovalEmail(employer); // 이메일 발송
    }

    private AdminEmployerRequestDto convertToDto(Employer employer) {
        return AdminEmployerRequestDto.builder()
                .employerId(employer.getEmployerId())
                .companyName(employer.getCompanyName())
                .bizRegNo(employer.getBizRegNo())
                .companyEmail(employer.getCompanyEmail())
                .createdAt(employer.getCreatedAt())
                .isApproved(employer.getIsApproved())
                .build();
    }

    private void sendApprovalEmail(Employer employer) {
        String toEmail = employer.getCompanyEmail();
        String subject = "기업 승인 완료 안내";
        String url = "http://localhost:3000/emp/info?employerId=" + employer.getEmployerId(); // 운영시 https로 변경

        Context context = new Context();
        context.setVariable("companyName", employer.getCompanyName());
        context.setVariable("signupUrl", url);

        mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
    }
}
