package com.career.careerlink.job.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateJobPostingRequest {

    private String title;
    private String description;
    private String jobFieldCode;
    private String locationCode;
    private String employmentTypeCode;
    private String educationLevelCode;
    private String careerLevelCode;
    private String salaryCode;
    private LocalDate applicationDeadline;
    private AgreementStatus isSkillsnap;
    private AgreementStatus isActive;
    private String employerId;
    private LocalDateTime updatedAt;
    private String updatedBy;
}