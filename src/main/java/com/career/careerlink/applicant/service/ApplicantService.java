package com.career.careerlink.applicant.service;

import com.career.careerlink.applicant.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    ApplicationResponseDto apply(ApplicationRequestDto requestDto);
    Page<ApplicationResponseDto> getMyApplications(String period, Pageable pageable);
    List<ApplicationResponseDto> getApplicationsByJobPosting(Integer jobPostingId);
    ApplicationResponseDto cancelApplication(Integer applicationId);
    ApplicationResponseDto reapply(Integer applicationId);
}
