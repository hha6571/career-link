package com.career.careerlink.users.service.impl;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.dto.SignupRequestDto;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.ApplicantRepository;
import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.TokenRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import com.career.careerlink.users.entity.LoginUser;
import com.career.careerlink.users.entity.enums.UserStatus;
import com.career.careerlink.users.repository.AdminRepository;
import com.career.careerlink.users.repository.LoginUserRepository;
import com.career.careerlink.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

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
                .updatedAt(dto.getUpdatedAt())
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
        return new TokenResponse(accessToken, refreshToken, expiresIn);
    }

    @Override
    public TokenResponse reissue(TokenRequestDto dto, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }

        String userId = jwtTokenProvider.getUserId(dto.getRefreshToken());
        String role = jwtTokenProvider.getRole(dto.getRefreshToken());
        String employerId = jwtTokenProvider.getEmployerId(dto.getRefreshToken());
        String redisRefresh = redisUtil.get("refresh:" + userId);

        if (!dto.getRefreshToken().equals(redisRefresh)) {
            throw new RuntimeException("불일치 리프레시 토큰");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role, employerId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, role, employerId);
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