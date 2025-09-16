package com.career.careerlink.employers.info.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmployerPublicProfileDto {

    private String employerId;
    private String companyName;
    private String companyLogoUrl;
    private String homepageUrl;
    private String companyIntro;
    private String locationCode;
    private String locationName;

    private long activePostingCount;   // 활성 공고 수
    private boolean hiring;            // activePostingCount > 0

    private List<JobPostingSummaryDto> recentPostings; // 최근/대표 공고 목록

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class JobPostingSummaryDto {
        private Long jobPostingId;
        private String title;

        private String jobFieldCode;
        private String jobFieldName;

        private String locationCode;
        private String locationName;

        private String employmentTypeCode;
        private String employmentTypeName;

        private String careerLevelCode;
        private String careerLevelName;

        private String salaryCode;
        private String salaryName;

        private LocalDate applicationDeadline;
        private long viewCount;
    }
}