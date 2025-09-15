package com.career.careerlink.applicant.controller;

import com.career.careerlink.applicant.dto.*;
import com.career.careerlink.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto dto) {
        applicantService.signup(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 계정관리
     */
    @GetMapping("/account/getProfile")
    public ApplicantDto getProfile() {
        return applicantService.getProfile();
    }

    @PutMapping("/account/updateProfile")
    public ApplicantDto updateProfile(@RequestBody ApplicantDto dto) {
        return applicantService.updateProfile(dto);
    }

    @PostMapping("/account/changePassword")
    public void changePassword(@RequestBody ApplicantRequestPassWordDto requestPassWordDto) {
        applicantService.changePassword(requestPassWordDto);
    }

    @PostMapping("/account/withdraw")
    public void withdraw() {
        applicantService.withdraw();
    }

    /**
     * 이력서
     */
    // 이력서 단건 조회
    @GetMapping("/resume/getResume/{resumeId}")
    public ResumeDto getResume(@PathVariable Integer resumeId) {
        return applicantService.getResume(resumeId);
    }

    // 내 이력서 전체 조회
    @GetMapping("/resume/getMyResumes")
    public List<ResumeDto> getMyResumes() {
        return applicantService.getMyResumes();
    }

    @PostMapping("/resume/createResume")
    public ResumeDto createResume(@RequestBody ResumeFormDto dto) {
        return applicantService.createResume(dto);
    }

    @PutMapping("/resume/updateResume/{resumeId}")
    public ResumeDto updateResume(@PathVariable Integer resumeId, @RequestBody ResumeFormDto dto) {
        return applicantService.updateResume(resumeId, dto);
    }

    @DeleteMapping("/resume/deleteResume/{resumeId}")
    public void deleteResume(@PathVariable Integer resumeId) {
        applicantService.deleteResume(resumeId);
    }

    /**
     * 자소서
     */
    @GetMapping("/coverLetter/getMyCoverLetters")
    public List<CoverLetterDto> getMyCoverLetters() {
        return applicantService.getMyCoverLetters();
    }
    @GetMapping("/coverLetter/getMyCoverLetter/{coverLetterId}")
    public CoverLetterDto getMyCoverLetter(@PathVariable("coverLetterId") Integer coverLetterId) {
        return applicantService.getMyCoverLetter(coverLetterId);
    }
    @PostMapping("/coverLetter/createCoverLetter")
    public CoverLetterDto createCoverLetter(@RequestBody CoverLetterDto dto) {
        return applicantService.createCoverLetter(dto);
    }
    @PutMapping("/coverLetter/updateCoverLetter/{coverLetterId}")
    public CoverLetterDto updateCoverLetter(
            @PathVariable("coverLetterId") Integer coverLetterId,
            @RequestBody CoverLetterDto dto) {
        return applicantService.updateCoverLetter(coverLetterId, dto);
    }
    @DeleteMapping("/coverLetter/deleteCoverLetter/{coverLetterId}")
    public void deleteCoverLetter(@PathVariable("coverLetterId") Integer coverLetterId) {
        applicantService.deleteCoverLetter(coverLetterId);
    }
    /**
     *  지원하기
     */
    @PostMapping("/job-postings/apply")
    public ApplicationResponseDto apply(@RequestBody ApplicationRequestDto requestDto) {
        return applicantService.apply(requestDto);
    }
    @GetMapping("/job-postings/getMyApplications")
    public Page<ApplicationResponseDto> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "3M") String period) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return applicantService.getMyApplications(period, pageable);
    }
    @GetMapping("/job-postings/getApplicationsByJobPosting/{jobPostingId}")
    public List<ApplicationResponseDto> getApplicationsByJobPosting(@PathVariable Integer jobPostingId) {
        return applicantService.getApplicationsByJobPosting(jobPostingId);
    }
    @PutMapping("/job-postings/cancel/{applicationId}")
    public ApplicationResponseDto cancelApplication(@PathVariable Integer applicationId) {
        return applicantService.cancelApplication(applicationId);
    }

    @PostMapping("/job-postings/reapply/{applicationId}")
    public ApplicationResponseDto reapply(@PathVariable Integer applicationId) {
        return applicantService.reapply(applicationId);
    }

}
