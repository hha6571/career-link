package com.career.careerlink.global.security.oauth;

import com.career.careerlink.global.exception.AccountAlreadyLinkedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    @Value("${app.frontend.base-url}") String frontBaseUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {
        if (ex instanceof AccountAlreadyLinkedException aale) {
            // 이미 다른 provider로 연결되어 있어 로그인 불가
            String url = frontBaseUrl + "/auth/social/error"
                    + "?status=ALREADY_LINKED"
                    + "&email=" + URLEncoder.encode(aale.getEmail(), StandardCharsets.UTF_8)
                    + "&existingProvider=" + URLEncoder.encode(aale.getExistingProvider(), StandardCharsets.UTF_8);
            res.sendRedirect(url);
            return;
        }
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
    }
}