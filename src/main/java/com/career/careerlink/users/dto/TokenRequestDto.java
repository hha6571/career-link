package com.career.careerlink.users.dto;

import lombok.*;

@Getter
@Setter
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}
