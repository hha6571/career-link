package com.career.careerlink.applicant.resume.dto;

import com.career.careerlink.applicant.coverLetter.dto.CoverLetterDto;
import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.common.enums.YnType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeFormDto {
    private String title;
    private YnType isActive;

    private List<EducationDto> educations;
    private List<ExperienceDto> experiences;
    private List<CertificationDto> certifications;
    private List<SkillDto> skills;
    private List<CoverLetterDto> coverLetters;

    public Resume toEntity(String userId) {
        return Resume.builder()
                .title(this.title)
                .isActive(this.isActive)
                .userId(userId)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

