package com.career.careerlink.employers.jobPosting.controller;

import com.career.careerlink.applicant.application.dto.ApplicationPreviewResponseDto;
import com.career.careerlink.employers.jobPosting.dto.*;
import com.career.careerlink.employers.jobPosting.service.EmpJobPostingService;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.job.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
@Validated
public class EmpJobPostingController {

    private final EmpJobPostingService empJobPostingService;
    private final JobPostingService jobPostingService;

    /**
     * 기업공고 리스트 조회
     */
    @GetMapping("/job-postings/manage")
    public Page<EmployerJobPostingResponse> getJobPostingList(EmployerJobPostingSearchRequest req, Principal principal) {
        String employerUserId = principal.getName();
        return jobPostingService.searchForEmployer(req, employerUserId);
    }

    /**
     * 기업공고 삭제처리 (다건)
     */
    @SkipWrap
    @PostMapping("/job-postings/delete-bulk")
    public int jobPostingDeleteBulk(@RequestBody List<String> targetJobPostingIds, Principal principal) {
        String employerUserId = principal.getName();
        return jobPostingService.deleteBulkByEmployer(targetJobPostingIds, employerUserId);
    }

    /**
     * 기업 공고 셀렉트박스용 단순 조회
     */
    @GetMapping("/job-postings")
    public List<JobPostingSimpleDto> getMyJobPostings(Principal principal) {
        String employerUserId = principal.getName();
        return empJobPostingService.getMyJobPostings(employerUserId);
    }

    /**
     * 지원자 목록 조회 (기업 소속 공고 기준)
     */
    @GetMapping("/applications")
    public Page<ApplicationDto> getApplications(ApplicationRequestDto req) {
        return empJobPostingService.getApplications(req);
    }

    /**
     * 지원 상태 업데이트
     */
    @PutMapping("/applications/status")
    public void updateApplicationStatuses(@RequestBody List<ApplicationDto> updates,
                                             Principal principal) {
        String employerUserId = principal.getName();
        empJobPostingService.updateStatuses(updates, employerUserId);
    }
    /**
     * 지원서 미리보기 (기업 전용)
     */
    @GetMapping("/applications/{applicationId}/preview")
    public ApplicationPreviewResponseDto getApplicationPreview(
            @PathVariable Integer applicationId,
            Principal principal) {
        String employerUserId = principal.getName();
        return empJobPostingService.getApplicationPreview(applicationId, employerUserId);
    }

}