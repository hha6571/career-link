package com.career.careerlink.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresAt;
    private String role;

    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn,String role) {
        return new TokenResponse(accessToken, refreshToken, expiresIn, role);
    }
}
