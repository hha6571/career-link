package com.career.careerlink.applicant.service;

import com.career.careerlink.applicant.dto.*;

import java.util.List;

public interface ApplicantService {
    void signup(SignupRequestDto dto);
    ApplicantDto getProfile();
    ApplicantDto updateProfile(ApplicantDto dto);
    void changePassword(ApplicantRequestPassWordDto requestPassWordDto);
    void withdraw();
    ResumeDto createResume(ResumeFormDto dto);
    ResumeDto getResume(Integer resumeId);
    List<ResumeDto> getMyResumes();
    ResumeDto updateResume(Integer resumeId, ResumeFormDto dto);
    void deleteResume(Integer resumeId);
}
