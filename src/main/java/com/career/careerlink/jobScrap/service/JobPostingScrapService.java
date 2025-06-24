package com.career.careerlink.jobScrap.service;

import com.career.careerlink.jobScrap.dto.JobPostingScrapDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingScrapService {
    void addScrap(String userId, Integer jobPostingId);
    void removeScrap(String userId, Integer jobPostingId);
    Page<JobPostingScrapDto> getMyScraps(String userId, Pageable pageable);
}
