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
    List<CoverLetterDto> getMyCoverLetters();
    CoverLetterDto getMyCoverLetter(Integer coverLetterId);
    CoverLetterDto createCoverLetter(CoverLetterDto dto);
    CoverLetterDto updateCoverLetter(Integer coverLetterId, CoverLetterDto dto);
    void deleteCoverLetter(Integer coverLetterId);
    ApplicationDto apply(ApplicationRequestDto requestDto); // 지원하기
    List<ApplicationDto> getMyApplications(); // 내 지원 내역
    List<ApplicationDto> getApplicationsByJobPosting(Integer jobPostingId);
}
