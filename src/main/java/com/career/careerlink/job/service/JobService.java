package com.career.careerlink.job.service;

import com.career.careerlink.job.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

public interface JobService {
    JobPostingResponse saveJobPosting(String employerUserId, CreateJobPostingRequest req);
    JobFiltersResponse getFilters();                                      // /filters
    Page<JobCardResponse> getJobList(JobSearchCond c, Pageable pageable); // /jobList
    JobPostingResponse detailJobPosting(@RequestParam int jobPostingId);
    void updateJobPosting(Integer jobPostingId, @Valid UpdateJobPostingRequest req);
}
