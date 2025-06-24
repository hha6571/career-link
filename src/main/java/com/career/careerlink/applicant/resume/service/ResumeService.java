package com.career.careerlink.applicant.resume.service;

import com.career.careerlink.applicant.resume.dto.ResumeDto;
import com.career.careerlink.applicant.resume.dto.ResumeFormDto;

import java.util.List;

public interface ResumeService {
    ResumeDto createResume(ResumeFormDto dto);
    ResumeDto getResume(Integer resumeId);
    List<ResumeDto> getMyResumes();
    ResumeDto updateResume(Integer resumeId, ResumeFormDto dto);
    void deleteResume(Integer resumeId);
}
