package com.career.careerlink.api.auth.controller;

import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.global.security.oauth.OnboardingStore;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.ApplicantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/social")
@RequiredArgsConstructor
public class SocialOnboardingController {
    private final OnboardingStore store;
    private final ApplicantRepository applicantRepository;
    private final JwtTokenProvider jwtTokenProvider; // 정식 토큰 발급용
    private final RedisUtil redisUtil;

    @GetMapping("/me")
    public OnboardingMeResponse me(@RequestParam("code") String code) {
        var payload = store.getByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, "expired"));
        return new OnboardingMeResponse(payload.getEmail(), payload.getName(), payload.getProvider());
    }

    @PostMapping("/complete")
    public LoginResponse complete(
            @RequestBody SocialCompleteRequest req,
            HttpServletRequest httpReq,
            HttpServletResponse res
    ) {
        var payload = store.getByCode(req.code())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, "expired"));

        // (선택) 약관/개인정보 필수 동의 체크
        if (req.agreeTerms() != YnType.Y || req.agreePrivacy() != YnType.Y) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수 약관에 동의해야 합니다.");
        }

        // 이메일 중복 검사 (이메일이 없을 수도 있음: 카카오 미동의 케이스)
        if (payload.getEmail() != null && applicantRepository.existsByEmail(payload.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        // 최초 INSERT
        var saved = applicantRepository.save(Applicant.builder()
                .userId(UserIdGenerator.generate("USR"))
                .email(payload.getEmail()) // null 허용 (추후 추가 수집)
                .userName(Optional.ofNullable(req.userName()).orElse(payload.getName()))
                .socialType(payload.getProvider())            // "GOOGLE"/"KAKAO" (대문자)
                .socialLoginId(payload.getProviderUserId())
                .phoneNumber(req.phoneNumber())
                .birthDate(req.birthDate())
                .agreeTerms(req.agreeTerms())
                .agreePrivacy(req.agreePrivacy())
                .agreeMarketing(req.agreeMarketing())
                .createdBy("system")
                .build());

        // 일회용 코드 소비 + providerKey 정리 (핵심)
        store.consumeAndCleanup(payload.getCode(), payload.getProvider(), payload.getProviderUserId());

        // 정식 토큰 발급 + 쿠키 심기
        String accessToken = jwtTokenProvider.createAccessToken(saved.getUserId(), "USER", null);
        String refreshToken = jwtTokenProvider.createRefreshToken(saved.getUserId(), "USER", null);
        long expiresIn = jwtTokenProvider.getRemainingTime(accessToken);

        // 로컬/프로덕션 판단 (OAuth2SuccessHandler와 동일 로직 권장)
        boolean isLocalHttp = "localhost".equalsIgnoreCase(httpReq.getServerName()) && !httpReq.isSecure();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000)
                .sameSite("Strict")
                .build();

        res.addHeader("Set-Cookie", accessCookie.toString());
        res.addHeader("Set-Cookie", refreshCookie.toString());

        redisUtil.set("refresh:" + saved.getUserId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        return new LoginResponse("ok");
    }

    public record OnboardingMeResponse(String email, String userName, String provider) {}

    public record SocialCompleteRequest(
            String code,
            String userName,
            String phoneNumber,
            LocalDate birthDate,
            YnType agreeTerms,
            YnType agreePrivacy,
            YnType agreeMarketing
    ) {}

    public record LoginResponse(String status) {}
}
