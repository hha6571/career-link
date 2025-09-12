package com.career.careerlink.job.service;

import com.career.careerlink.admin.dto.AdminJobPostingResponse;
import com.career.careerlink.admin.dto.AdminJobPostingSearchRequest;
import com.career.careerlink.employers.dto.EmployerJobPostingResponse;
import com.career.careerlink.employers.dto.EmployerJobPostingSearchRequest;
import com.career.careerlink.job.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface JobPostingService {
    JobPostingResponse saveJobPosting(String employerUserId, CreateJobPostingRequest req);
    JobFiltersResponse getFilters();                                      // /filters
    Page<JobCardResponse> getJobList(JobSearchCond c, Pageable pageable); // /jobList
    JobPostingResponse detailJobPosting(@RequestParam int jobPostingId);
    void updateJobPosting(Integer jobPostingId, @Valid UpdateJobPostingRequest req);

    // (기업/관리자) 공고조회 및 공고 게시글 삭제
    Page<EmployerJobPostingResponse> searchForEmployer(EmployerJobPostingSearchRequest req, String employerUserId);
    Page<AdminJobPostingResponse> searchForAdmin(AdminJobPostingSearchRequest req);
    int deleteBulkByEmployer(@NotBlank List<String>targetJobPostingIds, String employerUserId);
    int deleteBulkByAdmin(@NotBlank List<String>targetJobPostingIds);
    
    // HOT 100 공고 조회
    HotDtos.HotResponse getHot(HotDtos.HotRequest hotRequest);
}
