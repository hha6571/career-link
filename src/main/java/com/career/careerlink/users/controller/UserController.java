package com.career.careerlink.users.controller;

import com.career.careerlink.common.send.UserVerificationService;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.users.dto.*;
import com.career.careerlink.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

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

    /**
     * user 로그인
     */
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        return userService.login(dto, response);
    }

    /**
     * 로그인 연장(토큰 재발급)
     */
    @PostMapping("/reissue")
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        userService.reissue(request, response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
    }

    /**
     * 세션 검증
     */
    @GetMapping("/auth/me")
    public TokenResponse me(HttpServletRequest request) {
        return userService.me(request);
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
    @SkipWrap
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
    @SkipWrap
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

    /**
     * 휴면계정 인증번호 발송
     */
    @PostMapping("/reactivate/request")
    public void accountReact(@RequestBody AccountReactivateRequest request){
        userVerificationService.sendAccountVerificationCode(request.loginId());
    }

    /**
     * 휴면계정 인증번호 검증
     */
    @PostMapping("/reactivate/verify")
    public void verifyAccountCode(@RequestBody VerifyAccountCodeRequest request) {
       userVerificationService.verifyAccountCode(request.loginId(), request.code()
        );
    }
}