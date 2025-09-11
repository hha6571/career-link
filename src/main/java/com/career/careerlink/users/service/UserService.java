package com.career.careerlink.users.service;

import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.TokenRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    boolean isLoginIdDuplicate(String loginId);
    TokenResponse login(LoginRequestDto dto, HttpServletResponse response);
    TokenResponse reissue(TokenRequestDto dto, HttpServletResponse response);
    void logout(String accessToken);
    void resetPassword(String resetToken, String newPassword);
}

