package com.career.careerlink.users.service.impl;

import com.career.careerlink.admin.repository.AdminRepository;
import com.career.careerlink.employers.repository.EmployerUserRepository;
import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.SignupRequestDto;
import com.career.careerlink.users.dto.TokenRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import com.career.careerlink.users.entity.LoginUser;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.LoginUserRepository;
import com.career.careerlink.users.repository.UserRepository;
import com.career.careerlink.users.service.UserService;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.career.careerlink.global.util.UserIdGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoginUserRepository loginUserRepository;
    private final EmployerUserRepository employerUserRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public void signup(SignupRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());
        String generatedUserId = UserIdGenerator.generate("USR");

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

        userRepository.save(newApplicant);
    }

    @Override
    public TokenResponse login(LoginRequestDto dto, HttpServletResponse response) {
        LoginUser user = loginUserRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserPk(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserPk(), user.getRole());
        long expiresIn = jwtTokenProvider.getRemainingTime(accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 운영 배포땐 true로 설정
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000) // 초 단위
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", refreshCookie.toString());

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // 운영에선 true
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());

        redisUtil.set("refresh:" + user.getUserPk(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        switch (user.getRole()) {
            case "USER" -> {
                userRepository.findById(user.getLoginId())
                        .ifPresent(applicant -> {
                            applicant.setLastLoginAt(LocalDateTime.now());
                            userRepository.save(applicant);
                        });
            }
            case "EMP" -> {
                employerUserRepository.findById(user.getLoginId())
                        .ifPresent(employerUser -> {
                            employerUser.setLastLoginAt(LocalDateTime.now());
                            employerUserRepository.save(employerUser);
                        });
            }
            case "ADMIN" -> {
                adminRepository.findById(user.getLoginId())
                        .ifPresent(adminUser -> {
                            adminUser.setLastLoginAt(LocalDateTime.now());
                            adminRepository.save(adminUser);
                        });
            }
        }
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }

    @Override
    public TokenResponse reissue(TokenRequestDto dto, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }

        String userId = jwtTokenProvider.getUserId(dto.getRefreshToken());
        String role = jwtTokenProvider.getRole(dto.getRefreshToken());
        String redisRefresh = redisUtil.get("refresh:" + userId);

        if (!dto.getRefreshToken().equals(redisRefresh)) {
            throw new RuntimeException("불일치 리프레시 토큰");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, role);
        long expiresIn = jwtTokenProvider.getRemainingTime(newAccessToken);

        redisUtil.set("refresh:" + userId, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false) // 운영 배포땐 true로 설정
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000) // 초 단위
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", refreshCookie.toString());
        System.out.println("==================토큰 재발급 완료 ===========");
        return new TokenResponse(newAccessToken, newRefreshToken, expiresIn);
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
    System.out.println("==================로그아웃처리 완료 ===========");
    }
}