package com.career.careerlink.users.controller;

import com.career.careerlink.common.send.UserVerificationService;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.users.dto.*;
import com.career.careerlink.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserVerificationService userVerificationService;

    @SkipWrap
    @GetMapping("/check-id")
    public Map<String, Boolean> checkLoginId(@RequestParam String loginId) {
        boolean exists = userService.isLoginIdDuplicate(loginId);
        return Map.of("exists", exists);
    }

    /**
     * 회원가입시 휴대폰 인증번호 발송
     * @param request
     * @return
     */
    @SkipWrap
    @PostMapping("/send-sms")
    public SingleMessageSentResponse sendSms(@RequestBody PhoneVerifyRequest request) {
        return userVerificationService.certificateSMS(request.phoneNumber());
    }

    /**
     * 회원가입시 휴대폰 인증번호 검증
     * @param request
     * @return verifyCode
     */
    @SkipWrap
    @PostMapping("/verify-phone-code")
    public boolean verifyPhoneCode(@RequestBody VerifyPhoneCodeRequest request) {
        return userVerificationService.verifyPhoneCode(request.phoneNumber(), request.code());
    }

    /**
     * 회원가입 이메일 인증번호 발송
     * @param request
     */
    @PostMapping("/send-email-code")
    public void sendEmailCode(@RequestBody BasicVerificationRequest request) {
        userVerificationService.sendEmailVerification(request.userName(), request.email());
    }

    /**
     * 회원가입 이메일 인증번호 검증
     * @param request
     * @return verifyCode
     */
    @SkipWrap
    @PostMapping("/verify-email-code")
    public boolean verifyEmailCode(@RequestBody BasicVerifyCodeRequest request) {
        return userVerificationService.verifyEmailCode(request.userName(), request.email(), request.code());
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
//        return ResponseEntity.ok(userService.login(dto, response));
//    }
    /**
     * 변경 요약 / 사용 가이드
     *
     * 1) 컨트롤러 리턴
     *    - 굳이 ResponseEntity로 감싸지 말고, 도메인 객체(List/DTO/Page 등)만 그대로 반환.
     *    - GlobalResponseAdvice 가 자동으로 { header, body, (optional) pagination } 형식으로 래핑된다.
     *
     * 2) 상태코드 기본 규칙(컨트롤러가 별도 지정 안 했을 때)
     *    - GET    → 200 OK
     *    - POST   → 201 Created
     *    - PUT/PATCH → 200 OK
     *    - DELETE → body == null 이면 204 No Content, body 있으면 200 OK
     *
     * 3) ResponseEntity를 써야 하는 경우
     *    - 상태코드/헤더를 직접 지정해야 할 때만 ResponseEntity를 사용.
     *    - 이 경우에도 body는 동일하게 {header, body, pagination}으로 감싸져 내려간다.
     *    - 예) POST지만 200으로 내리고 싶다면:
     *        return ResponseEntity.ok(service.doSomething(...));
     *
     * 4) 문자열 응답
     *    - String을 리턴해도 기본적으로 JSON으로 래핑됨.
     *
     * 5) @SkipWrap (공통 래핑 건너뛰기)
     *    - 파일 다운로드, 외부 콜백 등 “바디 원문 그대로” 내려야 할 때만 사용.
     *    - 주의: SkipWrap을 쓰면 프런트 공통 인터셉터가 기대하는 포맷이 아닐 수 있다.
     *    - 예)
     *      @SkipWrapWW
     *      @GetMapping("/health")
     *      public String health() {
     *          return "OK";  // 래핑 없이 그대로 "OK"
     *      }
     *
     * 7) 요약
     *    - 기본은 “그냥 DTO 리턴” → Advice가 메시지/코드/페이지네이션까지 처리.
     *    - 특수한 상태/헤더가 필요하면 ResponseEntity.
     *    - 바디 원문 그대로 내려야 하면 @SkipWrap.
     */

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        return userService.login(dto, response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@CookieValue("refreshToken") String refreshToken,
                                                 @RequestHeader("Authorization") String accessToken,
                                                 HttpServletResponse response) {
        TokenRequestDto dto = new TokenRequestDto();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        return ResponseEntity.ok(userService.reissue(dto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

    /**
     * 아이디 찾기 인증번호 발송
     * @param request
     */
    @PostMapping("/send-id-code")
    public void sendIdCode(@RequestBody BasicVerificationRequest request) {
        userVerificationService.sendIdVerificationCode(request.userName(), request.email());
    }

    /**
     * 아이디 찾기 인증번호 검증
     * @param request
     * @return verifyCode
     */
    @PostMapping("/verify-id-code")
    public Map<String, String> verifyIdCode(@RequestBody BasicVerifyCodeRequest request) {
        return userVerificationService.verifyIdCode(request.userName(), request.email(), request.code());
    }

    /**
     * 비밀번호 찾기 인증번호 발송
     * @param request
     */
    @PostMapping("/send-pwd-code")
    public void sendPwdCode(@RequestBody PwdVerificationRequest request) {
       userVerificationService.sendPwdVerificationCode(request.userName(), request.email(), request.loginId());
    }

    /**
     * 비밀번호 찾기 인증번호 검증
     * @param request
     * @return 비밀번호 재설정을 위한 resetToken
     */
    @PostMapping("/verify-pwd-code")
    public Map<String, String> verifyPwdCode(@RequestBody VerifyPwdCodeRequest request) {
        String tempToken = userVerificationService.verifyPwdCode(
                request.userName(), request.email(), request.loginId(), request.code()
        );
        return Map.of("resetToken", tempToken);
    }

    /**
     * 비빌번호 재설정
     * @param request resetToken, newPassword
     */
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.resetToken(), request.newPassword());
    }
}