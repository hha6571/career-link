package com.career.careerlink.admin.user.service.impl;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import com.career.careerlink.admin.user.mapper.UsersMapper;
import com.career.careerlink.admin.user.service.AdminUserService;
import com.career.careerlink.admin.user.spec.EmployerSpecification;
import com.career.careerlink.common.send.MailService;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.employers.info.repository.EmployerRepository;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final UsersMapper usersMapper;

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
                .bizRegistrationUrl(employer.getBizRegistrationUrl())
                .companyEmail(employer.getCompanyEmail())
                .createdAt(employer.getCreatedAt())
                .isApproved(employer.getIsApproved())
                .build();
    }

    private void sendApprovalEmail(Employer employer) {
        String toEmail = employer.getCompanyEmail();
        String subject = "[CareerLinl] 기업 승인 완료 안내";
        String url = "http://localhost:3000/emp/signup?employerId=" + employer.getEmployerId(); // 운영시 https로 변경

        Context context = new Context();
        context.setVariable("companyName", employer.getCompanyName());
        context.setVariable("signupUrl", url);

        mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
    }

    @Override
    public Page<UsersDto> getUsers(UsersRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

       // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = usersMapper.usersCount(req);
        List<UsersDto> rows = usersMapper.getUsers(req, offset, safeSize);
        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public void saveUsers(List<UsersDto> list) {
        for (UsersDto u : list) {
            if ("EMP".equals(u.getRole())) {
                usersMapper.updateEmployerStatus(u.getUserPk(), u.getUserStatus());
            } else if ("USER".equals(u.getRole())) {
                usersMapper.updateApplicantStatus(u.getUserPk(), u.getUserStatus());
            }
        }
    }

}
