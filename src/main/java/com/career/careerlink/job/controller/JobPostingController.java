package com.career.careerlink.job.controller;

import com.career.careerlink.job.dto.*;
import com.career.careerlink.job.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
@Validated
public class JobPostingController {
    private final JobPostingService jobPostingService;

    // 필터 조회
    @GetMapping("/filters")
    public JobFiltersResponse filters() {
        return jobPostingService.getFilters();
    }

    //등록된 공고 리스트 조회
    @GetMapping("/jobList")
    public Page<JobCardResponse> jobList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> jobField,
            @RequestParam(required = false) List<String> location,
            @RequestParam(required = false) List<String> empType,
            @RequestParam(required = false) List<String> edu,
            @RequestParam(required = false) List<String> exp,
            @RequestParam(required = false) List<String> sal,
            Principal principal
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var cond = JobSearchCond.builder()
                .keyword(keyword)
                .jobField(jobField)
                .location(location)
                .empType(empType)
                .edu(edu)
                .exp(exp)
                .sal(sal)
                .build();
        String userId = (principal != null) ? principal.getName() : null;
        return jobPostingService.getJobList(cond, pageable, userId);
    }

    // 공고등록
    @PreAuthorize("hasRole('EMP')")
    @PostMapping("/job-posting/new")
    public JobPostingResponse saveJobPosting(
            @Valid @RequestBody CreateJobPostingRequest req,
            Authentication authentication) {

        String employerUserId = authentication.getName();
        return jobPostingService.saveJobPosting(employerUserId, req);
    }

    @GetMapping("/job-posting/detail")
    public JobPostingResponse detailJobPosting(@RequestParam(name = "id") int jobPostingId, Principal principal){
        String userId = (principal != null) ? principal.getName() : null;
        return jobPostingService.detailJobPosting(jobPostingId, userId);
    }

    @PreAuthorize("hasAnyRole('EMP','ADMIN')")
    @PutMapping("/job-posting/update")
    public void update(@RequestParam("id") Integer jobPostingId,@Valid @RequestBody UpdateJobPostingRequest req) {
        jobPostingService.updateJobPosting(jobPostingId, req);
    }

    @GetMapping("/job-postings/hot")
    public HotDtos.HotResponse hot(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String cursor,
            Principal principal
    ) {
        String userId = (principal != null) ? principal.getName() : null;
        return jobPostingService.getHot(new HotDtos.HotRequest(limit, cursor),userId);
    }
}
