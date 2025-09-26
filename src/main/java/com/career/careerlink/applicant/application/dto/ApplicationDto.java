package com.career.careerlink.applicant.application.dto;

import com.career.careerlink.applicant.application.entity.Application;
import com.career.careerlink.applicant.application.entity.enums.ApplicationStatus;
import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.job.entity.JobPosting;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 지원 생성/수정용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Integer jobPostingId;
    private Integer resumeId;
    private Integer coverLetterId;

    /** Request → Entity 변환 */
    public Application toEntity(JobPosting jobPosting, Resume resume, CoverLetter coverLetter, String userId, String resumeSnapshot,
                                String coverLetterSnapshot) {
        return Application.builder()
                .jobPosting(jobPosting)
                .resume(resume)
                .coverLetter(coverLetter)
                .userId(userId)
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(LocalDateTime.now())
                .resumeSnapshot(resumeSnapshot)
                .coverLetterSnapshot(coverLetterSnapshot)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /** Entity 업데이트 */
    public void updateEntity(Application entity, String userId, ApplicationStatus status) {
        entity.setStatus(status);
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
    }
}

