package com.career.careerlink.job.dto;

import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPostingResponse {
    private Integer jobPostingId;
    private String title;
    private String description;
    private String employerId;
    private String jobFieldCode;
    private String locationCode;
    private String employmentTypeCode;
    private String careerLevelCode;
    private String salaryCode;
    private LocalDate applicationDeadline;
    private AgreementStatus isActive;

    public static JobPostingResponse from(JobPosting posting) {
        return JobPostingResponse.builder()
                .jobPostingId(posting.getJobPostingId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .employerId(posting.getEmployerId())
                .jobFieldCode(posting.getJobFieldCode())
                .locationCode(posting.getLocationCode())
                .employmentTypeCode(posting.getEmploymentTypeCode())
                .careerLevelCode(posting.getCareerLevelCode())
                .salaryCode(posting.getSalaryCode())
                .applicationDeadline(posting.getApplicationDeadline())
                .isActive(posting.getIsActive())
                .build();
    }
}
