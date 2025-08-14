package com.career.careerlink.common.mail;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVerificationService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    // ---------------- 공통 메서드 ----------------
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void saveCodeToRedis(String key, String code) {
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    private void sendCodeMail(String email, String username, String code, String type, String subject, String template) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("code", code);
        context.setVariable("type", type);
        mailService.sendHtmlMail(email, subject, template, context);
    }

    // ---------------- 본인인증 ----------------
    public void sendEmailVerification(String email) {
        // 이메일 존재 여부만 체크
        Applicant applicant = userRepository.findByEmail(email)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        String code = generateCode();
        String type = "";
        saveCodeToRedis("auth:" + email, code);
        sendCodeMail(email, applicant.getUserName(), code, type, "[CareerLink] 본인 인증 코드", "email-verification");
    }

    public boolean verifyEmailCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get("auth:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }
        redisTemplate.delete("auth:" + email);
        return true;
    }

    // ---------------- 아이디 찾기 ----------------
    public void sendIdVerificationCode(String name, String email) {
        Applicant applicant = userRepository.findByUserNameAndEmail(name, email)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        String code = generateCode();
        String type = "아이디 찾기";
        saveCodeToRedis("findId:" + email, code);
        sendCodeMail(email, name, code, type, "[CareerLink] 아이디 찾기 인증번호 안내", "find-verification");
    }

    public Map<String, String> verifyIdCode(String name, String email, String code) {
        String savedCode = redisTemplate.opsForValue().get("findId:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }

        Applicant applicant = userRepository.findByUserNameAndEmail(name, email)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        redisTemplate.delete("findId:" + email);
        return Map.of("loginId", applicant.getLoginId());
    }

    // ---------------- 비밀번호 찾기 ----------------
    public void sendPwdVerificationCode(String name, String email, String loginId) {
        Applicant applicant = userRepository.findByUserNameAndEmailAndLoginId(name, email, loginId)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        String code = generateCode();
        String type = "비밀번호 재설정";
        saveCodeToRedis("findPwd:" + email, code);
        sendCodeMail(email, name, code, type, "[CareerLink] 비밀번호 재설정 인증번호 안내", "find-verification");
    }

    public String verifyPwdCode(String name, String email, String loginId, String code) {
        String savedCode = redisTemplate.opsForValue().get("findPwd:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }

        Applicant applicant = userRepository.findByUserNameAndEmailAndLoginId(name, email, loginId)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        redisTemplate.delete("findPwd:" + email);
        // 임시 토큰 생성
        String tempToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("resetPwdToken:" + tempToken, applicant.getUserId().toString(), 10, TimeUnit.MINUTES);

        return tempToken;
    }
}
