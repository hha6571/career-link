package com.career.careerlink.jobScrap.controller;

import com.career.careerlink.jobScrap.dto.JobPostingScrapDto;
import com.career.careerlink.jobScrap.service.JobPostingScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job/scrap")
@RequiredArgsConstructor
@Validated
public class JobPostingScrapController {

    private final JobPostingScrapService jobPostingScrapService;

    /**
     * 내가 스크랩한 공고 조회 (페이징)
     */
    @GetMapping("/myScraps")
    public Page<JobPostingScrapDto> getMyScraps(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Authentication authentication) {

        String userId = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        return jobPostingScrapService.getMyScraps(userId, pageable);
    }

    /**
     * 공고 스크랩 추가
     */
    @PostMapping("/addScrap/{jobPostingId}")
    public void addScrap(@PathVariable Integer jobPostingId,
                         Authentication authentication) {
        String userId = authentication.getName();
        jobPostingScrapService.addScrap(userId, jobPostingId);
    }

    /**
     * 공고 스크랩 삭제
     */
    @DeleteMapping("/removeScrap/{jobPostingId}")
    public void removeScrap(@PathVariable Integer jobPostingId,
                            Authentication authentication) {
        String userId = authentication.getName();
        jobPostingScrapService.removeScrap(userId, jobPostingId);
    }
}
