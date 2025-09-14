package com.career.careerlink.applicant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDto {
    private Integer jobPostingId;
    private Integer resumeId;
    private Integer coverLetterId; // optional
}
