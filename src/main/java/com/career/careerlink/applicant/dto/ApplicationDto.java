package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.*;
import com.career.careerlink.applicant.entity.enums.ApplicationStatus;
import com.career.careerlink.job.entity.JobPosting;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {

    private Integer applicationId;
    private Integer jobPostingId;
    private Integer resumeId;
    private Integer coverLetterId;
    private String userId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    /** 단건 변환 */
    public static ApplicationDto of(Application entity) {
        if (entity == null) return null;
        return ApplicationDto.builder()
                .applicationId(entity.getApplicationId())
                .jobPostingId(entity.getJobPosting().getJobPostingId())
                .resumeId(entity.getResume().getResumeId())
                .coverLetterId(entity.getCoverLetter() != null ? entity.getCoverLetter().getCoverLetterId() : null)
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /** 리스트 변환 */
    public static List<ApplicationDto> listOf(List<Application> entities) {
        return entities.stream()
                .map(ApplicationDto::of)
                .collect(Collectors.toList());
    }

    /** Request → Entity 변환 */
    public Application toEntity(JobPosting jobPosting, Resume resume, CoverLetter coverLetter, String userId) {
        return Application.builder()
                .jobPosting(jobPosting)
                .resume(resume)
                .coverLetter(coverLetter)
                .userId(userId)
                .status(ApplicationStatus.SUBMITTED)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    /** Entity 업데이트 */
    public void updateEntity(Application entity, String userId, ApplicationStatus status) {
        entity.setStatus(status);
        entity.setUpdatedBy(userId);
    }
}
