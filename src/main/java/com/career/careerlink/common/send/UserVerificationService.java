package com.career.careerlink.common.send;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.ApplicantRepository;
import com.career.careerlink.users.entity.LoginUser;
import com.career.careerlink.users.repository.AdminRepository;
import com.career.careerlink.users.repository.LoginUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserVerificationService {

    private final LoginUserRepository loginUserRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;
    private final ApplicantRepository applicantRepository;
    private final EmployerUserRepository employerUserRepository;
    private final AdminRepository adminRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.coolsms.api-key}")
    private String apiKey;

    @Value("${spring.coolsms.secret-key}")
    private String apiSecret;


    private static final long LINK_TTL_MIN = 15;    // 토큰 유효
    private static final long PENDING_TTL_MIN = 30; // 재전송 허용 대기
    private static final long COOLDOWN_SEC = 60;    // 재전송 쿨다운

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
        message.setFrom("");
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
        boolean exists = loginUserRepository.existsByEmail(email);
        if (exists) {
            throw new CareerLinkException(
                    ErrorCode.DUPLICATE_RESOURCE,
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

    // ---------------- 휴면계정 해제 ----------------
    public void sendAccountVerificationCode(String loginId) {
        String userName = loginUserRepository.findUserNameByLoginId(loginId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.UNAUTHORIZED, "해당 정보로 등록된 회원이 존재하지 않습니다."));

        String email = loginUserRepository.findEmailByLoginId(loginId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.UNAUTHORIZED, "해당 정보로 등록된 회원이 존재하지 않습니다."));

        String code = generateCode();
        String type = "휴면 계정 해제";
        saveCodeToRedis("reactivateId:" + loginId, code);
        sendCodeMail(email, userName, code, type, "[CareerLink] 휴면계정 해제 인증번호 안내", "find-verification");
    }

    @Transactional
    public void verifyAccountCode(String loginId, String code) {
        String savedCode = redisTemplate.opsForValue().get("reactivateId:" + loginId);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "인증번호가 일치하지 않거나 만료되었습니다.");
        }

        // 로그인용 뷰에서 역할 확인
        LoginUser loginUser = loginUserRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "해당 정보로 등록된 회원이 존재하지 않습니다."));

        // 업데이트
        int updated = switch (loginUser.getRole()) {
            case "USER"  -> applicantRepository.reactivateByLoginId(loginId);
            case "EMP"   -> employerUserRepository.reactivateByLoginId(loginId);
            case "ADMIN" -> adminRepository.reactivateByLoginId(loginId);
            default      -> 0;
        };

        if (updated == 0) {
            throw new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "휴면 해제 처리에 실패했습니다.");
        }

        // 일회용 코드 폐기
        redisTemplate.delete("reactivateId:" + loginId);
    }


    // ---------------- 기존 계정 social로그인 연결 메일 발송 ----------------
    public String sendSocialLink(String userId, String name, String email,
                                 String socialType, String providerUserId) {

        // 1) 사용자 정보는 DB에서 신뢰값으로 재조회 (파라미터 신뢰 X)
        Applicant user = applicantRepository.findById(userId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "올바른 접근이 아닙니다."));

        String safeEmail = user.getEmail();
        String safeName  = user.getUserName();

        // 2) 대기(pending) + 쿨다운 체크
        String pendingKey  = "linkSocial:pending:" + userId;
        String cooldownKey = "linkSocial:cooldown:" + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new CareerLinkException(ErrorCode.INVALID_REQUEST, "잠시 후 다시 시도해 주세요.");
        }
        // pending에 provider / providerUserId 저장 (재전송 때 사용)
        try {
            var pending = Map.of(
                    "provider", socialType == null ? null : socialType.toUpperCase(),
                    "providerUserId", providerUserId
            );
            redisTemplate.opsForValue().set(
                    pendingKey, objectMapper.writeValueAsString(pending), PENDING_TTL_MIN, TimeUnit.MINUTES
            );
        } catch (Exception e) {
            throw new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "대기 정보 저장 중 오류가 발생했습니다.");
        }
        // 쿨다운 부여
        redisTemplate.opsForValue().set(cooldownKey, "1", COOLDOWN_SEC, TimeUnit.SECONDS);

        // 3) 토큰 생성 + payload 저장
        String token = UUID.randomUUID().toString();
        var payload = new SocialLinkPayload(
                userId,
                safeEmail,
                socialType == null ? null : socialType.toUpperCase(),
                providerUserId,
                Instant.now().getEpochSecond()
        );
        String tokenKey = "linkSocial:token:" + token;
        try {
            String json = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(tokenKey, json, LINK_TTL_MIN, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "토큰 저장 중 오류가 발생했습니다.");
        }

        // 4) 링크 생성 (설정값 사용)
        String linkUrl = "http://localhost:8080/api/auth/link/confirm?token=" + token;

        // 5) 메일 발송 (템플릿에서 username, code=linkUrl, type 사용)
        String type = (socialType + " 소셜 로그인 계정 연결 동의 안내").trim();
        sendCodeMail(safeEmail, safeName, linkUrl, type,
                "[CareerLink] 소셜 로그인 계정 연결 동의 안내",
                "social-link-confirm");

        return token;
    }

    @Transactional
    public void confirmSocialLink(String token) {
        String key = "linkSocial:token:" + token;

        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "토큰이 만료되었거나 유효하지 않습니다.");
        }

        try {
            SocialLinkPayload p = objectMapper.readValue(json, SocialLinkPayload.class);

            boolean taken = applicantRepository
                    .existsBySocialTypeAndSocialLoginId(p.socialType(), p.providerUserId());
            if (taken) {
                redisTemplate.delete(key);
                throw new CareerLinkException(ErrorCode.INVALID_REQUEST, "이미 다른 계정에 해당 소셜이 연결되어 있습니다.");
            }

            Applicant user = applicantRepository.findById(p.userId())
                    .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "대상 계정을 찾을 수 없습니다."));

            user.setSocialType(p.socialType());
            user.setSocialLoginId(p.providerUserId());
            applicantRepository.save(user);

            // 1회성 토큰/대기 상태 정리
            redisTemplate.delete(key);
            redisTemplate.delete("linkSocial:pending:" + p.userId());     // ★ 추가
            redisTemplate.delete("linkSocial:cooldown:" + p.userId());    // 선택 (정리)

        } catch (CareerLinkException e) {
            throw e;
        } catch (Exception e) {
            throw new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "소셜 계정 연결 처리 중 오류가 발생했습니다.");
        }
    }

    public void resendSocialLinkByEmail(String email) {
        Applicant user = applicantRepository.findByEmail(email)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "계정을 찾을 수 없습니다."));
        String userId = user.getUserId();

        // 쿨다운
        String cooldownKey = "linkSocial:cooldown:" + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new CareerLinkException(ErrorCode.INVALID_REQUEST, "잠시 후 다시 시도해 주세요.");
        }

        // pending 조회
        String pendingKey = "linkSocial:pending:" + userId;
        String json = redisTemplate.opsForValue().get(pendingKey);
        if (json == null) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "재전송 가능한 요청이 없습니다. 다시 로그인으로 시도해 주세요.");
        }

        Map<?,?> p;
        try { p = objectMapper.readValue(json, Map.class); }
        catch (Exception e) { throw new CareerLinkException(ErrorCode.UNKNOWN_ERROR, "대기 정보 파싱 오류"); }

        String provider        = String.valueOf(p.get("provider"));
        String providerUserId  = String.valueOf(p.get("providerUserId"));

        sendSocialLink(userId, null, null, provider, providerUserId);
    }

    public static record SocialLinkPayload(
            String userId,
            String email,
            String socialType,
            String providerUserId,
            long issuedAtEpochSec
    ) {}
}
