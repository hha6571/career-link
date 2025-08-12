package com.career.careerlink.users.service.impl;

import com.career.careerlink.admin.repository.AdminRepository;
import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.employers.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
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
        return loginUserRepository.existsByLoginId(loginId);
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
//        LoginUser user = loginUserRepository.findByLoginId(dto.getLoginId())
//                .orElseThrow(() -> new CareerLinkException("사용자 없음"));
//
//        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
//            throw new CareerLinkException("비밀번호 불일치");
//        }
        /**
         *   @ 이 로직에서 예외를 이렇게 던지는 이유
         * - 계정/비밀번호가 틀린 경우는 "비즈니스 실패"이며 서버 오류(500)가 아님.
         * - 의도적으로 401(UNAUTHORIZED)을 내려야 하므로 CareerLinkException(ErrorCode.UNAUTHORIZED, message)를 던진다.
         * - 그러면 GlobalExceptionHandler가 잡아서
         *   HTTP 401 + { "header": { "result": false, "message": "...", "code": "UNAUTHORIZED" }, "body": null }
         *   형태로 응답을 표준화해준다.
         *
         *   @ 프론트에서는?
         * - Axios 인터셉터가 error.message 에 header.message를 넣어주도록 구성했기 때문에
         *   `notifyError(setSnackbar, err)` 한 줄로 스낵바에 메시지를 뿌릴 수 있다.
         *
         *   @ 보안 주의 (계정 유추 방지)
         * - 아이디/비밀번호가 틀린 케이스를 구분해 주면 계정 존재 여부가 노출될 수 있다.
         * - 운영 환경에서는 아래 "단일 메시지" 방식을 권장:
         *     "아이디 또는 비밀번호가 올바르지 않습니다."
         * - 필요시 내부 로그에는 구체 이유를 남기고 사용자 메시지는 단일 메시지로 고정.
         *
         *   @ 대안(참고)
         * - Spring Security 흐름을 그대로 태우고 싶다면 AuthenticationException을 던지고
         *   AuthenticationEntryPoint에서 401 응답을 만들 수도 있다.
         * - 지금 방식은 도메인 서비스에서 “의도된 401/403/409 …”를 명확하게 던지는 패턴이라 단순하고 테스트도 쉽다.
         */
        // --- 단일 메시지 권장안(운영 권장) ---
        LoginUser user = loginUserRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CareerLinkException(
                    ErrorCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // -- 만약 교육/개발 단계에서만 구분 메시지를 쓰고 싶다면 아래처럼 나눠도 됨:
        // .orElseThrow(() -> new CareerLinkException(ErrorCode.UNAUTHORIZED, "아이디가 올바르지 않습니다."));
        // if (!passwordEncoder.matches(...)) {
        //     throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        // }

        // (이하 토큰 발급/쿠키 세팅/레디스 저장/마지막 로그인 시간 갱신 등 기존 로직 그대로)
        // ...

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