package com.career.careerlink.job.dto;

import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.job.entity.JobPosting;
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
    private YnType isActive;
    private YnType isDeleted;

    Boolean scrapped;

    //스크랩여부 포함 안함
    public static JobPostingResponse from(JobPosting posting) {
        return from(posting, null); // null or false로 기본 처리
    }

    public static JobPostingResponse from(JobPosting posting, Boolean scrapped) {
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
                .scrapped(scrapped)
                .build();
    }

    public JobPostingResponse(
            Integer jobPostingId,
            String title,
            String description,
            String employerId,
            String companyName,
            String jobFieldCode,
            String educationLevelCode,
            String locationCode,
            String employmentTypeCode,
            String careerLevelCode,
            String salaryCode,
            LocalDate applicationDeadline,
            YnType isActive,
            YnType isDeleted
    ) {
        this.jobPostingId = jobPostingId;
        this.title = title;
        this.description = description;
        this.employerId = employerId;
        this.companyName = companyName;
        this.jobFieldCode = jobFieldCode;
        this.educationLevelCode = educationLevelCode;
        this.locationCode = locationCode;
        this.employmentTypeCode = employmentTypeCode;
        this.careerLevelCode = careerLevelCode;
        this.salaryCode = salaryCode;
        this.applicationDeadline = applicationDeadline;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }
}
