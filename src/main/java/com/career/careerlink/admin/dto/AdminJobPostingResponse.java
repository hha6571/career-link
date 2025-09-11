package com.career.careerlink.admin.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminJobPostingResponse {
    private String jobPostingId;
    private String employerId;
    private String companyName;
    private String title;
    private String applicationDeadline;
    private String isActive;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private AgreementStatus isDeleted;
}