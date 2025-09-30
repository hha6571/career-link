package com.career.careerlink.global.security.oauth;

import com.career.careerlink.common.send.UserVerificationService;
import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.ApplicantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final OnboardingStore onboardingStore;
    private final ApplicantRepository applicantRepository;
    private final RedisUtil redisUtil;
    private final UserVerificationService verification;
    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    @Value("${app.frontend.base-url}") String frontBaseUrl;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${app.cookie.same-site:Strict}")
    private String cookieSameSite;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication)
            throws IOException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = principal.getAttributes();

        String provider = null;
        if (authentication instanceof OAuth2AuthenticationToken authToken) {
            provider = normalizeProvider(authToken.getAuthorizedClientRegistrationId()); // GOOGLE / KAKAO
        }

        String providerUserId = getProviderUserId(provider, attrs);
        String email          = extractEmail(provider, attrs);
        String name           = extractName(provider, attrs);

        if (provider == null || providerUserId == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid OAuth2 user info");
            return;
        }

        // 1) 기존에 연결된 사용자 찾기
        Optional<Applicant> byLinkOpt = applicantRepository.findBySocialTypeAndSocialLoginId(provider, providerUserId);
        if (byLinkOpt.isPresent()) {
            var a = byLinkOpt.get();
            issueTokensAndRedirect(req, res, a.getUserId());
            a.setLastLoginAt(LocalDateTime.now());
            applicantRepository.save(a);
            return;
        }

        // 2) 이메일로 기존 계정 여부 확인
        Optional<Applicant> byEmailOpt = (email == null ? Optional.empty() : applicantRepository.findByEmail(email));
        if (byEmailOpt.isPresent()) {
            var a = byEmailOpt.get();

            // 기존 계정인데 소셜 로그인이 처음일 경우 메일로 연동인증
            if (a.getSocialType() == null || a.getSocialType().isBlank()) {
                verification.sendSocialLink(
                        a.getUserId(),
                        a.getUserName(),
                        a.getEmail(),
                        provider,
                        providerUserId
                );

                res.sendRedirect(frontBaseUrl + "auth/social/check-email?email=" +
                        URLEncoder.encode(a.getEmail(), StandardCharsets.UTF_8));
                return;
            }

            // 기존 계정인데 소셜로그인 providerUserId만 다를 경우, 갱신 후 로그인 허용
            if (a.getSocialType().equalsIgnoreCase(provider)) {
                a.setSocialLoginId(providerUserId);
                applicantRepository.save(a);
                issueTokensAndRedirect(req, res, a.getUserId());
                return;
            }

            // 이미 다른 provider로 연결되어 있을 경우, 해당 소셜 로그인 이용하기를 안내
            res.sendRedirect(frontBaseUrl + "auth/social/error?reason=provider_mismatch");
            return;
        }

        // 3) 기존 계정 없음, (필수 정보 기입 후) 신규 회원가입
        String code = UUID.randomUUID().toString();
        OnboardingPayload payload = OnboardingPayload.builder()
                .code(code)
                .provider(provider)
                .providerUserId(providerUserId)
                .email(email)
                .name(name)
                .build();

        onboardingStore.saveReplacingPrevious(payload);

        log.info("ISSUE code={} provider={} providerUserId={} email={} name={}",
                code, provider, providerUserId, email, name);

        res.sendRedirect(frontBaseUrl + "auth/social/onboarding?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8));
    }

    private void issueTokensAndRedirect(HttpServletRequest req, HttpServletResponse res, String userId) throws IOException {
        String role = "USER";
        String employerId = null;

        String accessToken  = jwtTokenProvider.createAccessToken(userId, role, employerId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, role, employerId);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(cookieSecure).path("/")
                .domain(".careerlink.online")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000)
                .sameSite(cookieSameSite).build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true).secure(cookieSecure).path("/")
                .domain(".careerlink.online")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite).build();

        res.addHeader("Set-Cookie", refreshCookie.toString());
        res.addHeader("Set-Cookie", accessCookie.toString());

        redisUtil.set("refresh:" + userId, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());
        res.sendRedirect(frontBaseUrl + "main");
    }

    private static String normalizeProvider(String id) {
        if (id == null) return null;
        return id.trim().toUpperCase();
    }

    private static String getProviderUserId(String provider, Map<String, Object> attrs) {
        if (provider == null) {
            return asString(attrs.getOrDefault("providerUserId", attrs.getOrDefault("id", attrs.get("sub"))));
        }
        switch (provider) {
            case "GOOGLE":
                return asString(attrs.getOrDefault("sub", attrs.get("id")));
            case "KAKAO":
                return asString(attrs.getOrDefault("id", attrs.get("sub")));
            default:
                return asString(attrs.getOrDefault("id", attrs.getOrDefault("providerUserId", attrs.get("sub"))));
        }
    }

    @SuppressWarnings("unchecked")
    private static String extractEmail(String provider, Map<String, Object> attrs) {
        String direct = asString(attrs.get("email"));
        if (direct != null) return direct;
        if ("KAKAO".equals(provider)) {
            Object acc = attrs.get("kakao_account");
            if (acc instanceof Map<?, ?>) {
                Object email = ((Map<String, Object>) acc).get("email");
                return asString(email);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String extractName(String provider, Map<String, Object> attrs) {
        String direct = asString(attrs.getOrDefault("name", attrs.get("nickname")));
        if (direct != null) return direct;
        if ("KAKAO".equals(provider)) {
            Object acc = attrs.get("kakao_account");
            if (acc instanceof Map<?, ?>) {
                Object profile = ((Map<String, Object>) acc).get("profile");
                if (profile instanceof Map<?, ?>) {
                    Object nickname = ((Map<String, Object>) profile).get("nickname");
                    String n = asString(nickname);
                    if (n != null) return n;
                }
            }
        }
        return null;
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
