package com.career.careerlink.job.service;

import com.career.careerlink.job.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

public interface JobService {
    JobPostingResponse saveJobPosting(String employerUserId, EmployerCreateJobPostingRequest req);
    JobFiltersResponse getFilters();                                      // /filters
    Page<JobCardResponse> getJobList(JobSearchCond c, Pageable pageable); // /jobList
    JobPostingResponse detailJobPosting(@RequestParam int jobPostingId);
}
