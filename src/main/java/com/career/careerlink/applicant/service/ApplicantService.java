package com.career.careerlink.applicant.service;

import com.career.careerlink.applicant.dto.LoginRequestDto;
import com.career.careerlink.applicant.dto.SignupRequestDto;
import com.career.careerlink.applicant.dto.TokenRequestDto;
import com.career.careerlink.applicant.dto.TokenResponse;

public interface ApplicantService {
    boolean isLoginIdDuplicate(String loginId);
    TokenResponse login(LoginRequestDto dto);
    TokenResponse reissue(TokenRequestDto dto);
    void logout(String accessToken);
    void signup(SignupRequestDto dto);
}

