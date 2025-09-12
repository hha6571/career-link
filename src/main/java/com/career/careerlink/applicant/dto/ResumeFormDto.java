package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.Resume;
import com.career.careerlink.applicant.entity.enums.YnType;
import lombok.*;

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
                .updatedBy(userId)
                .build();
    }
}

