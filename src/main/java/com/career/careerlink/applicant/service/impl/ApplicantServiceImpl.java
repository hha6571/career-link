package com.career.careerlink.applicant.service.impl;

import com.career.careerlink.applicant.dto.LoginRequestDto;
import com.career.careerlink.applicant.dto.SignupRequestDto;
import com.career.careerlink.applicant.dto.TokenRequestDto;
import com.career.careerlink.applicant.dto.TokenResponse;
import com.career.careerlink.applicant.entity.Applicant;
import com.career.careerlink.applicant.entity.enums.AgreementStatus;
import com.career.careerlink.applicant.repository.ApplicantRepository;
import com.career.careerlink.applicant.service.ApplicantService;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.career.careerlink.global.util.UserIdGenerator;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    Applicant entity = new Applicant();

    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return applicantRepository.existsByLoginId(loginId);
    }

    @Override
    public void signup(SignupRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());
        String generatedUserId = UserIdGenerator.generate();

        Applicant newApplicant = Applicant.builder()
                .userId(generatedUserId)
                .loginId(dto.getLoginId())
                .password(encodedPassword)
                .socialType(dto.getSocialType())
                .socialLoingId(dto.getSocialLoingId())
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .userType(dto.getUserType())
                .email(dto.getEmail())
                .lastLoginAt(dto.getLastLoginAt())
                .dormantAt(dto.getDormantAt())
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .userStatus(dto.getUserStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        applicantRepository.save(newApplicant);
    }

    @Override
    public TokenResponse login(LoginRequestDto dto) {
        Applicant user = applicantRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        redisUtil.set("refresh:" + user.getUserId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse reissue(TokenRequestDto dto) {
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }

        String userId = jwtTokenProvider.getUserId(dto.getRefreshToken());
        String redisRefresh = redisUtil.get("refresh:" + userId);

        if (!dto.getRefreshToken().equals(redisRefresh)) {
            throw new RuntimeException("불일치 리프레시 토큰");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken();

        redisUtil.set("refresh:" + userId, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String accessToken) {
        String token = accessToken.replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰");
        }

        String userId = jwtTokenProvider.getUserId(token);
        redisUtil.delete("refresh:" + userId);

        long remainTime = jwtTokenProvider.getRemainingTime(token);
        redisUtil.set("blacklist:" + token, "logout", remainTime);
    }
}