package com.career.careerlink.api.auth.controller;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.common.send.UserVerificationService;
import com.career.careerlink.global.exception.CareerLinkException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/link")
@RequiredArgsConstructor
public class SocialLinkController {
    private final UserVerificationService verification;
    @Value("${app.frontend.base-url}") String frontBaseUrl;

    @PostMapping("/resend")
    public Map<String, Object> resend(@RequestBody Map<String,String> r) {
        String email = r.get("email");
        if (email == null || email.isBlank()) {
            throw new CareerLinkException(ErrorCode.INVALID_REQUEST, "email이 필요합니다.");
        }
        verification.resendSocialLinkByEmail(email);
        return Map.of("sent", true);
    }

    @GetMapping("/confirm")
    public void confirm(@RequestParam String token,
                        HttpServletResponse res) throws IOException {
        try {
            verification.confirmSocialLink(token); // DB 연결 완료
            // 성공 시 안내 페이지로
            res.sendRedirect(frontBaseUrl + "auth/social/complete?status=success");
        } catch (CareerLinkException e) {
            // 실패 시 에러 안내 페이지로
            String reason = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            res.sendRedirect(frontBaseUrl + "auth/social/complete?status=error&reason=" + reason);
        }
    }

}
