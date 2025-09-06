package com.career.careerlink.job.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerCreateJobPostingRequest {
    private Integer jobPostingId;
    private String employerId;
    private String title;
    private String description;
    private String jobFieldCode;
    private String locationCode;
    private String employmentTypeCode;
    private String careerLevelCode;
    private String salaryCode;
    private LocalDate applicationDeadline;
    private AgreementStatus isSkillsnap;
    private AgreementStatus isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
