package com.career.careerlink.admin.dto;

import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminEmployerRequestDto {
    private String employerId;
    private String companyName;
    private String bizRegNo;
    private String companyEmail;
    private LocalDateTime createdAt;
    private AgreementStatus isApproved;
}
