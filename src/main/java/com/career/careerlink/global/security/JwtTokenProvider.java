package com.career.careerlink.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final long accessTokenExpiration = 1000 * 60 * 60; // 60분
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 14; // 14일

    public String createAccessToken(String userId, String role, String employerId) {
        var builder = Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey);

        if (employerId != null && !employerId.isBlank()) {
            builder.claim("employerId", employerId);
        }

        return builder.compact();
    }

    public String createRefreshToken(String userId, String role, String employerId) {
        var builder = Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey);

        if (employerId != null && !employerId.isBlank()) {
            builder.claim("employerId", employerId);
        }

        return builder.compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public String getEmployerId(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("employerId", String.class);
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRemainingTime(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        String role = getRole(token); // 👈 토큰에서 role 추출

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role); // 예: ROLE_ADMIN
        return new UsernamePasswordAuthenticationToken(userId, "", List.of(authority));
    }

    // 기업 승인 메일 유효시간
    public String createEmployerSignupToken(String employerId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000); // 3일

        return Jwts.builder()
                .setSubject(employerId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
