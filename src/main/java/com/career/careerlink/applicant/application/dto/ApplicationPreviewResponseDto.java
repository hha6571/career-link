package com.career.careerlink.applicant.application.dto;

import com.career.careerlink.applicant.coverLetter.dto.CoverLetterSnapshotDto;
import com.career.careerlink.applicant.resume.dto.ResumeSnapshotDto;
import lombok.*;

/**
 * 지원서 미리보기 DTO
 * Employer → Snapshot 기반 조회 전용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationPreviewResponseDto {
    private ResumeSnapshotDto resume;
    private CoverLetterSnapshotDto coverLetter;
}


