package com.career.careerlink.employers.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerJobPostingResponse {
    private String jobPostingId;
    private String employerId;
    private String title;
    private String applicationDeadline;
    private String isActive;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private AgreementStatus isDeleted;
}