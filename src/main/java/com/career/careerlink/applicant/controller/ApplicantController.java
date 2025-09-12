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
    //이력서 단건 조회
    @GetMapping("/resume/getResume/{resumeId}")
    public ResumeDto getResume(@PathVariable Integer resumeId) {
        return applicantService.getResume(resumeId);
    }
    //내 이력서 전체 조회
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
}
