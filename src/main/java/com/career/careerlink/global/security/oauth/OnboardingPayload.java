package com.career.careerlink.global.security.oauth;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingPayload {
    private String code;            // UUID
    private String provider;        // "GOOGLE"/"KAKAO" (대문자)
    private String providerUserId;  // 공급자 고유 id (string화)
    private String email;           // null 가능(카카오 미동의)
    private String name;            // 닉네임/이름
}
