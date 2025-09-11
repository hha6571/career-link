package com.career.careerlink.common.send;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.users.entity.LoginUser;
import com.career.careerlink.users.repository.LoginUserRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
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

    private final LoginUserRepository loginUserRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    @Value("${spring.coolsms.api-key}")
    private String apiKey;

    @Value("${spring.coolsms.secret-key}")
    private String apiSecret;

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
    // 회원가입 본인인증 인증코드 발송(휴대폰)
    public SingleMessageSentResponse certificateSMS(String phoneNumber) {
        String code = generateCode();
        saveCodeToRedis("verify:" + phoneNumber, code);

        String savedCode = redisTemplate.opsForValue().get("verify:" + phoneNumber);
        DefaultMessageService messageService =
                NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

        Message message = new Message();
        message.setFrom("01073075221");
        message.setTo(phoneNumber);
        message.setText("[CareerLink] 인증번호 [" + code + "]를 입력해 주세요.");
        message.setType(MessageType.SMS);

        return messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    // 회원가입 본인인증 인증번호 검증(휴대폰)
    public boolean verifyPhoneCode(String phoneNumber, String code) {
        String savedCode = redisTemplate.opsForValue().get("verify:" + phoneNumber);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }
        redisTemplate.delete("verify:" + phoneNumber);
        return true;
    }

    // 회원가입 본인인증 인증코드 발송(이메일)
    public void sendEmailVerification(String userName, String email) {
        // 이메일 존재 여부만 체크
        boolean exists = loginUserRepository.findByEmail(email).isPresent();
        if (exists) {
            throw new CareerLinkException(
                    ErrorCode.DUPLICATE_RESOURCE, // 새로운 에러코드 사용
                    "해당 정보로 이미 회원이 존재합니다."
            );
        }
        
        String code = generateCode();
        String type = "회원가입 본인인증";
        saveCodeToRedis("authEmail:" + email, code);
        sendCodeMail(email, userName, code, type, "[CareerLink] 본인 인증 코드", "find-verification");
    }

    // 회원가입 본인인증 인증번호 검증(이메일)
    public boolean verifyEmailCode(String userName, String email, String code) {
        String savedCode = redisTemplate.opsForValue().get("authEmail:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }
        redisTemplate.delete("authEmail:" + email);
        return true;
    }

    // ---------------- 아이디 찾기 ----------------
    public void sendIdVerificationCode(String name, String email) {
        LoginUser applicant = loginUserRepository.findByUserNameAndEmail(name, email)
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

        LoginUser applicant = loginUserRepository.findByUserNameAndEmail(name, email)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        redisTemplate.delete("findId:" + email);
        return Map.of("loginId", applicant.getLoginId());
    }

    // ---------------- 비밀번호 찾기 ----------------
    public void sendPwdVerificationCode(String name, String email, String loginId) {
        LoginUser user = loginUserRepository.findByUserNameAndEmailAndLoginId(name, email, loginId)
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

        LoginUser user = loginUserRepository.findByUserNameAndEmailAndLoginId(name, email, loginId)
                .orElseThrow(() -> new CareerLinkException(
                        ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다.")
                );

        redisTemplate.delete("findPwd:" + email);
        // 임시 토큰 생성
        String tempToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("resetPwdToken:" + tempToken, user.getUserPk(), 10, TimeUnit.MINUTES);

        return tempToken;
    }
}
