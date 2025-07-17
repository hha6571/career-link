package com.career.careerlink.applicant.controller;

import com.career.careerlink.applicant.dto.LoginRequestDto;
import com.career.careerlink.applicant.dto.SignupRequestDto;
import com.career.careerlink.applicant.dto.TokenRequestDto;
import com.career.careerlink.applicant.dto.TokenResponse;
import com.career.careerlink.applicant.entity.enums.AgreementStatus;
import com.career.careerlink.applicant.repository.ApplicantRepository;
import com.career.careerlink.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/applicants")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicantController.class);

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestParam String loginId) {
        boolean exists = applicantService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto dto) {
        logger.debug("====================회원가입페이지 ==============");
        applicantService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(applicantService.login(dto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequestDto dto) {
        return ResponseEntity.ok(applicantService.reissue(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        applicantService.logout(token);
        return ResponseEntity.ok().build();
    }
}