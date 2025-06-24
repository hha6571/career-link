package com.career.careerlink.applicant.application.dto;

import com.career.careerlink.applicant.application.entity.Application;
import com.career.careerlink.applicant.application.entity.enums.ApplicationStatus;
import com.career.careerlink.applicant.resume.entity.Resume;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 지원 현황 조회용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {

    private Integer applicationId;
    private Integer jobPostingId;
    private String jobTitle;
    private String companyName;

    private Integer resumeId;
    private String resumeTitle; // snapshot 기반
    private Integer coverLetterId;

    private String userId;
    private ApplicationStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    private LocalDate applicationDeadline; // 공고 마감일자

    /** 단건 변환 */
    public static ApplicationResponseDto of(Application entity) {
        if (entity == null) return null;

        String resumeTitle = null;
        if (entity.getResumeSnapshot() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Resume snapshot = mapper.readValue(entity.getResumeSnapshot(), Resume.class);
                resumeTitle = snapshot.getTitle();
            } catch (Exception e) {
                resumeTitle = entity.getResume() != null ? entity.getResume().getTitle() : null;
            }
        }

        return ApplicationResponseDto.builder()
                .applicationId(entity.getApplicationId())
                .jobPostingId(entity.getJobPosting().getJobPostingId())
                .jobTitle(entity.getJobPosting().getTitle()) // 공고명은 FK 그대로 사용
                .companyName(entity.getJobPosting().getEmployer().getCompanyName())
                .resumeId(entity.getResume().getResumeId())
                .resumeTitle(resumeTitle) // snapshot 기반
                .coverLetterId(entity.getCoverLetter() != null ? entity.getCoverLetter().getCoverLetterId() : null)
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .applicationDeadline(entity.getJobPosting().getApplicationDeadline())
                .build();
    }

    /** 리스트 변환 */
    public static List<ApplicationResponseDto> listOf(List<Application> entities) {
        return entities.stream()
                .map(ApplicationResponseDto::of)
                .collect(Collectors.toList());
    }
}

