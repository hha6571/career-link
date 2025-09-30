package com.career.careerlink.jobScrap.dto;

import com.career.careerlink.job.dto.JobPostingResponse;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.jobScrap.entity.JobPostingScrap;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingScrapDto {

    private Integer scrapId;
    private String userId;
    private Integer jobPostingId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private JobPostingResponse jobPosting;

    // == 변환 메서드 ==
    public static JobPostingScrapDto of(JobPostingScrap entity) {
        return JobPostingScrapDto.builder()
                .scrapId(entity.getScrapId())
                .userId(entity.getUserId())
                .jobPostingId(entity.getJobPosting().getJobPostingId())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .jobPosting(JobPostingResponse.from(entity.getJobPosting()))
                .build();
    }

    public static List<JobPostingScrapDto> of(List<JobPostingScrap> entities) {
        return entities.stream().map(JobPostingScrapDto::of).collect(Collectors.toList());
    }

    public JobPostingScrap toEntity() {
        return JobPostingScrap.builder()
                .scrapId(this.scrapId)
                .userId(this.userId)
                .jobPosting(
                        JobPosting.builder()
                                .jobPostingId(this.jobPostingId)
                                .build()
                )
                .createdBy(this.createdBy)
                .build();
    }
}
