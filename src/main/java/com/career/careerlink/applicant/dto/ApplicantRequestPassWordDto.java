package com.career.careerlink.applicant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantRequestPassWordDto {
    private String currentPassword;
    private String newPassword;
}
