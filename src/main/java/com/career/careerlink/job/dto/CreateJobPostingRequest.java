package com.career.careerlink.job.dto;

import com.career.careerlink.common.enums.YnType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateJobPostingRequest {
    private Integer jobPostingId;
    private String employerId;
    private String title;
    private String description;
    private String jobFieldCode;
    private String locationCode;
    private String employmentTypeCode;
    private String educationLevelCode;
    private String careerLevelCode;
    private String salaryCode;
    private LocalDate applicationDeadline;
    private YnType isSkillsnap;
    private YnType isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private YnType isDeleted;
}
