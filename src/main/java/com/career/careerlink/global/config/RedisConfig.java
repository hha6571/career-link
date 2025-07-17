package com.career.careerlink.global.config;

import com.career.careerlink.applicant.dto.LoginRequestDto;
import com.career.careerlink.applicant.dto.TokenRequestDto;
import com.career.careerlink.applicant.dto.TokenResponse;
import com.career.careerlink.applicant.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    private final ApplicantService applicantService;

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