package com.career.careerlink.users.service;

import com.career.careerlink.users.dto.LoginRequestDto;
import com.career.careerlink.users.dto.SignupRequestDto;
import com.career.careerlink.users.dto.TokenRequestDto;
import com.career.careerlink.users.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void signup(SignupRequestDto dto);
    boolean isLoginIdDuplicate(String loginId);
    TokenResponse login(LoginRequestDto dto, HttpServletResponse response);
    void reissue(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    TokenResponse me(HttpServletRequest request);
    void resetPassword(String resetToken, String newPassword);
}

