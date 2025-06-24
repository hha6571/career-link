package com.career.careerlink.users.service.impl;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.global.util.CookieUtil;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.SignupRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.entity.LoginUser;
import com.career.careerlink.users.entity.enums.UserStatus;
import com.career.careerlink.users.repository.AdminRepository;
import com.career.careerlink.users.repository.ApplicantRepository;
import com.career.careerlink.users.repository.LoginUserRepository;
import com.career.careerlink.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ApplicantRepository applicantRepository;
    private final LoginUserRepository loginUserRepository;
    private final EmployerUserRepository employerUserRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Strict}")
    private String cookieSameSite;

    @Override
    public void signup(SignupRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());
        String generatedUserId = UserIdGenerator.generate("USR");

        Applicant newApplicant = Applicant.builder()
                .userId(generatedUserId)
                .loginId(dto.getLoginId())
                .passwordHash(encodedPassword)
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
                .createdBy(generatedUserId)
                .build();

        applicantRepository.save(newApplicant);
    }

    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return loginUserRepository.existsByLoginId(loginId);
    }

    @Override
    public TokenResponse login(LoginRequestDto dto, HttpServletResponse response) {
        LoginUser user = loginUserRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CareerLinkException(
                    ErrorCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        UserStatus status = user.getUserStatus();
        if (status == UserStatus.WITHDRAWN) {
            throw new CareerLinkException(ErrorCode.ACCOUNT_DELETED, "탈퇴 처리된 계정입니다.");
        }
        if (status == UserStatus.DORMANT || user.getDormantAt() != null) {
            throw new CareerLinkException(ErrorCode.ACCOUNT_DORMANT, "휴면계정입니다. 본인확인 후 해제해주세요.");
        }

        String employerId = null;

        if ("EMP".equals(user.getRole())) {
            var empOpt = employerUserRepository.findEmployerIdByEmployerUserId(user.getUserPk());
            if (empOpt.isPresent() && empOpt.get().getEmployerId() != null) {
                employerId = empOpt.get().getEmployerId();
            }
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserPk(), user.getRole(), employerId);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserPk(), user.getRole(), employerId);
        long expiresIn = jwtTokenProvider.getRemainingTime(accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure) // 운영 배포땐 true로 설정
                .path("/")
                .domain(".careerlink.online")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000) // 초 단위
                .sameSite(cookieSameSite)
                .build();

        response.setHeader("Set-Cookie", refreshCookie.toString());

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(cookieSecure) // 운영에선 true
                .path("/")
                .domain(".careerlink.online")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());

        redisUtil.set("refresh:" + user.getUserPk(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        switch (user.getRole()) {
            case "USER" -> {
                applicantRepository.findById(user.getUserPk())
                        .ifPresent(applicant -> {
                            applicant.setLastLoginAt(LocalDateTime.now());
                            applicantRepository.save(applicant);
                        });
            }
            case "EMP" -> {
                employerUserRepository.findById(user.getUserPk())
                        .ifPresent(employerUser -> {
                            employerUser.setLastLoginAt(LocalDateTime.now());
                            employerUserRepository.save(employerUser);
                        });
            }
            case "ADMIN" -> {
                adminRepository.findById(user.getUserPk())
                        .ifPresent(adminUser -> {
                            adminUser.setLastLoginAt(LocalDateTime.now());
                            adminRepository.save(adminUser);
                        });
            }
        }
        return new TokenResponse(expiresIn, employerId, user.getRole());
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다.");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);
        String employerId = jwtTokenProvider.getEmployerId(refreshToken);

        String redisRefresh = redisUtil.get("refresh:" + userId);
        if (redisRefresh == null || !refreshToken.equals(redisRefresh)) {
            throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다.");
        }

        String newAccess = jwtTokenProvider.createAccessToken(userId, role, employerId);
        String newRefresh = jwtTokenProvider.createRefreshToken(userId, role, employerId);

        redisUtil.set("refresh:" + userId, newRefresh, jwtTokenProvider.getRefreshTokenExpiration());

        ResponseCookie accessCookie = cookieUtil.buildCookie(
                "accessToken", newAccess, jwtTokenProvider.getAccessTokenExpiration() / 1000);
        ResponseCookie refreshCookie = cookieUtil.buildCookie(
                "refreshToken", newRefresh, jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getCookieValue(request, "accessToken");

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String userId = jwtTokenProvider.getUserId(accessToken);
            redisUtil.delete("refresh:" + userId);

            long remainTime = jwtTokenProvider.getRemainingTime(accessToken);
            redisUtil.set("blacklist:" + accessToken, "logout", remainTime);
        }

        // 쿠키 삭제
        response.addHeader("Set-Cookie", cookieUtil.deleteCookie("accessToken").toString());
        response.addHeader("Set-Cookie", cookieUtil.deleteCookie("refreshToken").toString());
    }

    public TokenResponse me(HttpServletRequest request) {
        String accessToken = getCookieValue(request, "accessToken");
        if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
            throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다.");
        }

        // 로그아웃 블랙리스트 체크
        String black = redisUtil.get("blacklist:" + accessToken);
        if (black != null) {
            throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다.");
        }

        String role = jwtTokenProvider.getRole(accessToken);
        String employerId = jwtTokenProvider.getEmployerId(accessToken);

        long remainingMs = jwtTokenProvider.getRemainingTime(accessToken); // TTL(ms)
        long expiresAtMs = System.currentTimeMillis() + Math.max(0L, remainingMs);

        return TokenResponse.builder()
                .role(role)
                .employerId(employerId)
                .accessTokenExpiresAt(expiresAtMs)
                .build();
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * 비밀번호 재설정
     * @param resetToken
     * @param newPassword
     */
    @Override
    public void resetPassword(String resetToken, String newPassword) {
        String userIdStr = redisTemplate.opsForValue().get("resetPwdToken:" + resetToken);
        if (userIdStr == null) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "비밀번호 재설정 토큰이 유효하지 않거나 만료되었습니다.");
        }

        Applicant applicant = applicantRepository.findByUserId(userIdStr)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        applicant.setPasswordHash(encodedPassword);
        applicantRepository.save(applicant);

        // 토큰 삭제 (1회 사용 원칙)
        redisTemplate.delete("resetPwdToken:" + resetToken);
    }
}