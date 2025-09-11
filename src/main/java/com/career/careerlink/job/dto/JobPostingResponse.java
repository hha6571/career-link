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
    private String companyName;
    private String jobFieldCode;
    private String educationLevelCode;
    private String locationCode;
    private String employmentTypeCode;
    private String careerLevelCode;
    private String salaryCode;
    private LocalDate applicationDeadline;
    private AgreementStatus isActive;
    private AgreementStatus isDeleted;

    public static JobPostingResponse from(JobPosting posting) {
        return JobPostingResponse.builder()
                .jobPostingId(posting.getJobPostingId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .employerId(posting.getEmployerId())
                .companyName(posting.getEmployer() != null ? posting.getEmployer().getCompanyName() : null)
                .jobFieldCode(posting.getJobFieldCode())
                .locationCode(posting.getLocationCode())
                .employmentTypeCode(posting.getEmploymentTypeCode())
                .careerLevelCode(posting.getCareerLevelCode())
                .salaryCode(posting.getSalaryCode())
                .applicationDeadline(posting.getApplicationDeadline())
                .isActive(posting.getIsActive())
                .isDeleted(posting.getIsDeleted())
                .build();
    }
}
