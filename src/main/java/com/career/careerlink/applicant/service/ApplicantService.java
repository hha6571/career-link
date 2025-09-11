package com.career.careerlink.applicant.service;

import com.career.careerlink.applicant.dto.ApplicantDto;
import com.career.careerlink.applicant.dto.ApplicantRequestPassWordDto;
import com.career.careerlink.applicant.dto.SignupRequestDto;

public interface ApplicantService {
    void signup(SignupRequestDto dto);
    ApplicantDto getProfile();
    ApplicantDto updateProfile(ApplicantDto dto);
    void changePassword(ApplicantRequestPassWordDto requestPassWordDto);
    void withdraw();
}
