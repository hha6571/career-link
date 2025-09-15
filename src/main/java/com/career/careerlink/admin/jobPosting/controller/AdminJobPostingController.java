package com.career.careerlink.admin.jobPosting.controller;

import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingResponse;
import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingSearchRequest;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.job.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/job-postings")
@RequiredArgsConstructor
public class AdminJobPostingController {

    private final JobPostingService jobPostingService;

    /**
     * 기업공고 리스트 조회
     */
    @GetMapping("/manage")
    public Page<AdminJobPostingResponse> getJobPostingList(AdminJobPostingSearchRequest req) {
        return jobPostingService.searchForAdmin(req);
    }

    /**
     * 기업공고 삭제처리 (다건)
     */
    @SkipWrap
    @PostMapping("/delete-bulk")
    public int jobPostingDeleteBulk(@RequestBody List<String> targetJobPostingIds) {
        return jobPostingService.deleteBulkByAdmin(targetJobPostingIds);
    }
}
