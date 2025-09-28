package com.career.careerlink.global.security.oauth;


import com.career.careerlink.global.exception.AccountAlreadyLinkedException;
import com.career.careerlink.global.security.oauth.userinfo.GoogleUserInfo;
import com.career.careerlink.global.security.oauth.userinfo.KakaoUserInfo;
import com.career.careerlink.global.security.oauth.userinfo.OAuth2UserInfo;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UnifiedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final ApplicantRepository applicantRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User loaded = new DefaultOAuth2UserService().loadUser(req);
        String regId = req.getClientRegistration().getRegistrationId().toLowerCase();

        OAuth2UserInfo info = switch (regId) {
            case "google" -> new GoogleUserInfo(loaded.getAttributes());
            case "kakao"  -> new KakaoUserInfo(loaded.getAttributes());
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"), "Unsupported provider: " + regId);
        };

        if (info.getEmail() == null || info.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_required"), info.getProvider() + " email consent required");
        }

        // 1) 우리 시스템에 이미 있는 사용자 찾기
        Optional<Applicant> bySocial =
                applicantRepository.findBySocialTypeAndSocialLoginId(info.getProvider(), info.getProviderUserId());
        Optional<Applicant> byEmail =
                applicantRepository.findByEmail(info.getEmail());

        Applicant existing = bySocial.or(() -> byEmail).orElse(null);

        // 2) 기존 사용자, 다른 소셜 계정으로 등록되어있는 아이디일 경우
        if (existing != null
                && existing.getSocialType() != null
                && !existing.getSocialType().equals(info.getProvider())
        ) {
            throw new AccountAlreadyLinkedException(existing.getEmail(), existing.getSocialType());
        }

        // 3) 기존 사용자
        if (existing != null) {
            boolean linked = existing.getSocialType() != null && !existing.getSocialType().isBlank()
                    && existing.getSocialLoginId() != null && !existing.getSocialLoginId().isBlank();
            return toOAuth2UserForExisting(existing, req, info, linked);
        }

        // 4) 신규 사용자
        return toOAuth2UserForNew(req, info);
    }

    /** 기존 사용자용 **/
    private OAuth2User toOAuth2UserForExisting(Applicant a, OAuth2UserRequest req, OAuth2UserInfo info, boolean linked) {
        var authorities = List.of(new SimpleGrantedAuthority(linked ? "ROLE_USER" : "ROLE_OAUTH2"));
        String nameKey = userNameAttribute(req, info);

        Map<String, Object> attrs = new HashMap<>(info.getRaw());
        attrs.put("userId", a.getUserId());
        attrs.put("email", a.getEmail());
        attrs.put("name", a.getUserName());
        attrs.put("provider", info.getProvider());
        attrs.put("providerUserId", info.getProviderUserId());
        attrs.put("isNew", false);
        attrs.put("role", linked ? "USER" : "OAUTH2");
        attrs.put("linkRequired", !linked);

        return new DefaultOAuth2User(authorities, attrs, nameKey);
    }

    /** 신규 사용자용 임시 권한만 부여 */
    private OAuth2User toOAuth2UserForNew(OAuth2UserRequest req, OAuth2UserInfo info) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_OAUTH2"));
        String nameKey = userNameAttribute(req, info);

        Map<String, Object> attrs = new HashMap<>(info.getRaw());
        attrs.put("email", info.getEmail());
        attrs.put("name", info.getName());
        attrs.put("provider", info.getProvider());
        attrs.put("providerUserId", info.getProviderUserId());
        attrs.put("isNew", true);

        return new DefaultOAuth2User(authorities, attrs, nameKey);
    }

    private String userNameAttribute(OAuth2UserRequest req, OAuth2UserInfo info) {
        return Optional.ofNullable(req.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName())
                .orElse(info.getProvider().equalsIgnoreCase("google") ? "sub" : "id");
    }
}