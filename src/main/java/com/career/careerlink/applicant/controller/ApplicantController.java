package com.career.careerlink.applicant.controller;

import com.career.careerlink.applicant.dto.*;
import com.career.careerlink.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
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
    public ApplicationDto apply(@RequestBody ApplicationRequestDto requestDto) {
        return applicantService.apply(requestDto);
    }

    // 내 지원 내역
    @GetMapping("/job-postings/getMyApplications")
    public List<ApplicationDto> getMyApplications() {
        return applicantService.getMyApplications();
    }

    // 특정 공고 지원자 목록 (기업용)
    @GetMapping("/job-postings/getApplicationsByJobPosting/{jobPostingId}")
    public List<ApplicationDto> getApplicationsByJobPosting(@PathVariable Integer jobPostingId) {
        return applicantService.getApplicationsByJobPosting(jobPostingId);
    }

}
