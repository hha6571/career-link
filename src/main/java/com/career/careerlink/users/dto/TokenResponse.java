package com.career.careerlink.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenResponse {
    private long accessTokenExpiresAt;
    private String employerId;
    private String role;

    public static TokenResponse of(long expiresIn, String employerId, String role) {
        return new TokenResponse(expiresIn, employerId, role);
    }
}
