package com.career.careerlink.applicant.application.service.impl;

import com.career.careerlink.applicant.application.dto.ApplicationRequestDto;
import com.career.careerlink.applicant.application.dto.ApplicationResponseDto;
import com.career.careerlink.applicant.application.entity.Application;
import com.career.careerlink.applicant.application.entity.enums.ApplicationStatus;
import com.career.careerlink.applicant.application.repository.ApplicationRepository;
import com.career.careerlink.applicant.application.service.ApplicationService;
import com.career.careerlink.applicant.coverLetter.dto.CoverLetterSnapshotDto;
import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import com.career.careerlink.applicant.coverLetter.repository.CoverLetterRepository;
import com.career.careerlink.applicant.resume.dto.ResumeSnapshotDto;
import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.applicant.resume.repository.ResumeRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.repository.JobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {


    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public ApplicationResponseDto apply(ApplicationRequestDto requestDto) {
        String userId = getCurrentUserId();

        // 1. 취소되지 않은 지원이 이미 존재하는지 확인
        boolean exists = applicationRepository
                .existsByUserIdAndJobPosting_JobPostingIdAndStatusNot(
                        userId,
                        requestDto.getJobPostingId(),
                        ApplicationStatus.CANCELLED
                );

        if (exists) {
            throw new CareerLinkException("이미 해당 공고에 지원했습니다. (취소 후 다시 지원 가능)");
        }

        // 2. 공고 조회
        JobPosting jobPosting = jobRepository.findById(requestDto.getJobPostingId())
                .orElseThrow(() -> new CareerLinkException("공고를 찾을 수 없습니다."));

        // 3. 이력서 조회
        Resume resume = resumeRepository.findWithDetailsByResumeId(requestDto.getResumeId())
                .orElseThrow(() -> new CareerLinkException("이력서를 찾을 수 없습니다."));

        // 4. 자소서 조회 (선택적)
        CoverLetter coverLetter = null;
        if (requestDto.getCoverLetterId() != null) {
            coverLetter = coverLetterRepository.findById(requestDto.getCoverLetterId())
                    .orElseThrow(() -> new CareerLinkException("자소서를 찾을 수 없습니다."));
        }

        // 5. JSON 변환 (snapshot 저장용)
        String resumeJson;
        String coverLetterJson = null;

        try {
            ResumeSnapshotDto resumeSnap = ResumeSnapshotDto.of(resume);
            resumeJson = objectMapper.writeValueAsString(resumeSnap);

            if (coverLetter != null) {
                CoverLetterSnapshotDto clSnap = CoverLetterSnapshotDto.of(coverLetter);
                coverLetterJson = objectMapper.writeValueAsString(clSnap);
            }
        } catch (Exception  e) {
            throw new CareerLinkException("스냅샷 변환 중 오류가 발생했습니다.");
        }



        // 6. Application 생성 & 저장
        Application application = Application.builder()
                .jobPosting(jobPosting)
                .resume(resume)
                .coverLetter(coverLetter)
                .userId(userId)
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(LocalDateTime.now())
                .resumeSnapshot(resumeJson)           // 스냅샷 저장
                .coverLetterSnapshot(coverLetterJson) // 스냅샷 저장
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        return ApplicationResponseDto.of(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDto> getMyApplications(String period, Pageable pageable) {
        String userId = getCurrentUserId();

        Page<Application> pageResult;

        switch (period) {
            case "3M":
                pageResult = applicationRepository.findByUserIdAndAppliedAtAfter(
                        userId,
                        LocalDateTime.now().minusMonths(3),
                        pageable
                );
                break;

            case "6M":
                pageResult = applicationRepository.findByUserIdAndAppliedAtAfter(
                        userId,
                        LocalDateTime.now().minusMonths(6),
                        pageable
                );
                break;

            case "1Y":
                pageResult = applicationRepository.findByUserIdAndAppliedAtAfter(
                        userId,
                        LocalDateTime.now().minusYears(1),
                        pageable
                );
                break;

            case "ALL":
            default:
                pageResult = applicationRepository.findByUserId(userId, pageable);
                break;
        }

        return pageResult.map(ApplicationResponseDto::of);
    }

    @Override
    @Transactional
    public ApplicationResponseDto cancelApplication(Integer applicationId) {
        String userId = getCurrentUserId();

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CareerLinkException("지원 내역을 찾을 수 없습니다."));

        // 본인 지원 내역만 취소 가능
        if (!application.getUserId().equals(userId)) {
            throw new CareerLinkException("본인의 지원만 취소할 수 있습니다.");
        }

        // 기업에서 상태 변경했으면 취소 불가
        if (application.getStatus() != ApplicationStatus.SUBMITTED &&
                application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new CareerLinkException("기업에서 진행 중인 지원은 취소할 수 없습니다.");
        }

        application.setStatus(ApplicationStatus.CANCELLED);
        application.setUpdatedBy(userId);
        application.setUpdatedAt(LocalDateTime.now());

        return ApplicationResponseDto.of(application);
    }

    @Override
    @Transactional
    public ApplicationResponseDto reapply(Integer applicationId) {
        String userId = getCurrentUserId();

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CareerLinkException("지원 내역을 찾을 수 없습니다."));

        // 본인 지원 내역만 다시 지원 가능
        if (!application.getUserId().equals(userId)) {
            throw new CareerLinkException("본인의 지원만 다시 지원할 수 있습니다.");
        }

        // 마감일 체크
        JobPosting jobPosting = application.getJobPosting();
        if (jobPosting.getApplicationDeadline() != null &&
                jobPosting.getApplicationDeadline().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new CareerLinkException("이미 마감된 공고입니다. 다시 지원할 수 없습니다.");
        }

        // 취소 상태일 때만 다시 지원 가능
        if (application.getStatus() != ApplicationStatus.CANCELLED) {
            throw new CareerLinkException("취소된 지원만 다시 지원할 수 있습니다.");
        }

        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setAppliedAt(LocalDateTime.now()); // 지원일자 갱신
        application.setUpdatedBy(userId);
        application.setUpdatedAt(LocalDateTime.now());

        return ApplicationResponseDto.of(application);
    }
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CareerLinkException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }
}
