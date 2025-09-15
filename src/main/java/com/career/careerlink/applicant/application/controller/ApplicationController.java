package com.career.careerlink.applicant.application.controller;

import com.career.careerlink.applicant.application.dto.ApplicationRequestDto;
import com.career.careerlink.applicant.application.dto.ApplicationResponseDto;
import com.career.careerlink.applicant.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    /**
     *  지원하기
     */
    @PostMapping("/application/job-postings/apply")
    public ApplicationResponseDto apply(@RequestBody ApplicationRequestDto requestDto) {
        return applicationService.apply(requestDto);
    }
    @GetMapping("/application/job-postings/getMyApplications")
    public Page<ApplicationResponseDto> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "3M") String period) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return applicationService.getMyApplications(period, pageable);
    }
    @PutMapping("/application/job-postings/cancel/{applicationId}")
    public ApplicationResponseDto cancelApplication(@PathVariable Integer applicationId) {
        return applicationService.cancelApplication(applicationId);
    }

    @PostMapping("/application/job-postings/reapply/{applicationId}")
    public ApplicationResponseDto reapply(@PathVariable Integer applicationId) {
        return applicationService.reapply(applicationId);
    }

}
