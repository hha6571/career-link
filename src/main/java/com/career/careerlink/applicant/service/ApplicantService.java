package com.career.careerlink.applicant.service;

import com.career.careerlink.applicant.dto.ApplicantDto;
import com.career.careerlink.applicant.dto.ApplicantRequestPassWordDto;

public interface ApplicantService {
    ApplicantDto getProfile();
    ApplicantDto updateProfile(ApplicantDto dto);
    void changePassword(ApplicantRequestPassWordDto requestPassWordDto);
    void withdraw();
}
