package com.career.careerlink.employers.jobPosting.service.impl;

import com.career.careerlink.applicant.application.dto.ApplicationPreviewResponseDto;
import com.career.careerlink.applicant.application.entity.Application;
import com.career.careerlink.applicant.application.repository.ApplicationRepository;
import com.career.careerlink.applicant.coverLetter.dto.CoverLetterSnapshotDto;
import com.career.careerlink.applicant.resume.dto.ResumeSnapshotDto;
import com.career.careerlink.employers.jobPosting.dto.ApplicationDto;
import com.career.careerlink.employers.jobPosting.dto.ApplicationRequestDto;
import com.career.careerlink.employers.jobPosting.dto.JobPostingSimpleDto;
import com.career.careerlink.employers.jobPosting.mapper.ApplicationMapper;
import com.career.careerlink.employers.jobPosting.service.EmpJobPostingService;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.users.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpJobPostingServiceImpl implements EmpJobPostingService {

    private final EmployerUserRepository employerUserRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;


    private String resolveEmployerId(String employerUserId) {
        EmployerUsers eu = employerUserRepository.findByEmployerUserIdAndIsApproved(employerUserId, AgreementStatus.Y)
                .orElseThrow(() -> new RuntimeException("승인된 기업 회원을 찾을 수 없습니다."));
        return eu.getEmployerId();
    }
    @Override
    public List<JobPostingSimpleDto> getMyJobPostings(String employerUserId) {
        String employerId = resolveEmployerId(employerUserId);
        return applicationMapper.findJobPostingsByEmployerId(employerId);
    }

    @Override
    public Page<ApplicationDto> getApplications(ApplicationRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = applicationMapper.getApplicationCount(req);
        List<ApplicationDto> rows = applicationMapper.getApplications(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public void updateStatuses(List<ApplicationDto> updates, String employerUserId) {
        for (ApplicationDto dto : updates) {
            applicationMapper.updateApplicationStatus(dto.getApplicationId(),
                    dto.getStatus(),
                    employerUserId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationPreviewResponseDto getApplicationPreview(Integer applicationId, String employerUserId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CareerLinkException("지원 내역을 찾을 수 없습니다."));

        ResumeSnapshotDto resume = null;
        CoverLetterSnapshotDto coverLetter = null;

        try {
            if (application.getResumeSnapshot() != null) {
                resume = objectMapper.readValue(application.getResumeSnapshot(), ResumeSnapshotDto.class);
            }
            if (application.getCoverLetterSnapshot() != null) {
                coverLetter = objectMapper.readValue(application.getCoverLetterSnapshot(), CoverLetterSnapshotDto.class);
            }
        } catch (Exception e) {
            throw new CareerLinkException("스냅샷 복원 중 오류가 발생했습니다.");
        }

        return ApplicationPreviewResponseDto.builder()
                .resume(resume)
                .coverLetter(coverLetter)
                .build();
    }
}