package com.career.careerlink.employers.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Integer applicationId;   // 지원 ID
    private Integer jobPostingId;    // 공고 ID
    private String jobTitle;         // 공고 제목
    private Integer resumeId;
    private Integer coverLetterId;
    private String applicantId;      // 지원자 ID
    private String applicantName;    // 지원자 이름
    private String email;            // 지원자 이메일

    private String resumeTitle;      // 제출한 이력서 제목
    private String status;           // 지원 상태 (SUBMITTED, UNDER_REVIEW, PASSED, FAILED, CANCELLED)

    private LocalDateTime appliedAt; // 지원일
    private LocalDateTime updatedAt; // 상태 변경일
}
