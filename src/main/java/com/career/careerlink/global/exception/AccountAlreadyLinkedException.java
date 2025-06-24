package com.career.careerlink.global.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class AccountAlreadyLinkedException extends OAuth2AuthenticationException {
    private final String email;
    private final String existingProvider;

    public AccountAlreadyLinkedException(String email, String existingProvider) {
        super(new OAuth2Error("link_required"), "Account link required");
        this.email = email;
        this.existingProvider = existingProvider;
    }
    public String getEmail() { return email; }
    public String getExistingProvider() { return existingProvider; }
}