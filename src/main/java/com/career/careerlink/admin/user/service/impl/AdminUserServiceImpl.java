package com.career.careerlink.admin.user.service.impl;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.AdminEmployersSearchRequest;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import com.career.careerlink.admin.user.mapper.EmployerManageMapper;
import com.career.careerlink.admin.user.mapper.UsersMapper;
import com.career.careerlink.admin.user.service.AdminUserService;
import com.career.careerlink.common.send.MailService;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.employers.info.repository.EmployerRepository;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final UsersMapper usersMapper;
    private final EmployerManageMapper employerManageMapper;

    @Override
    public Page<AdminEmployerRequestDto> getEmployers(AdminEmployersSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        // 정렬 방향 보정
        String direction = Optional.ofNullable(req.getDirection())
                .orElse("asc")
                .toUpperCase(Locale.ROOT);
        direction = "DESC".equals(direction) ? "DESC" : "ASC";

        // 정렬 필드 보정(화이트리스트 키)
        String sort = Optional.ofNullable(req.getSort()).orElse("jobPostingId");

        long total = employerManageMapper.employersCount(req);
        List<AdminEmployerRequestDto> rows = employerManageMapper.getEmployers(req, offset, safeSize, sort, direction);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }


    @Override
    public void approveEmployer(String employerId, String adminUserId) {
        approveEmployerBulk(List.of(employerId), adminUserId);
    }

    @Override
    @Transactional
    public int approveEmployerBulk(List<String> targetEmployerIds, String adminUserId){
        if (targetEmployerIds == null || targetEmployerIds.isEmpty()) return 0;

        int updated = employerManageMapper.approveIfPendingBulk(targetEmployerIds, adminUserId);
        List<Employer> approvedList = employerRepository.findAllById(targetEmployerIds);
        // 커밋 이후 메일 발송
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override public void afterCommit() { sendApprovalEmail(approvedList); }
                }
        );

        return updated;
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

    private void sendApprovalEmail(List<Employer> employers) {
        for (Employer employer : employers) {
            try {
                String toEmail = employer.getCompanyEmail();
                String subject = "[CareerLink] 기업 승인 완료 안내";
                String url = "https://careerlink.online/emp/signup?employerId=" + employer.getEmployerId();

                Context context = new Context();
                context.setVariable("companyName", employer.getCompanyName());
                context.setVariable("signupUrl", url);

                mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
            } catch (Exception ex) {
            }
        }
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
