package com.career.careerlink.applicant.controller;

import com.career.careerlink.applicant.dto.ApplicantDto;
import com.career.careerlink.applicant.dto.ApplicantRequestPassWordDto;
import com.career.careerlink.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;

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
}
