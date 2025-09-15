package com.career.careerlink.applicant.resume.dto;

import com.career.careerlink.applicant.resume.entity.Experience;
import com.career.careerlink.applicant.resume.entity.Resume;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceDto {
    private Integer experienceId;
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public static ExperienceDto of(Experience entity) {
        return ExperienceDto.builder()
                .experienceId(entity.getExperienceId())
                .companyName(entity.getCompanyName())
                .position(entity.getPosition())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .build();
    }

    public static List<ExperienceDto> listOf(List<Experience> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(ExperienceDto::of).collect(Collectors.toList());
    }

    public Experience toEntity(Resume resume, String createdBy) {
        return Experience.builder()
                .experienceId(this.experienceId)
                .resume(resume)
                .companyName(this.companyName)
                .position(this.position)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .description(this.description)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    public void updateEntity(Experience entity, String updatedBy) {
        entity.setCompanyName(this.companyName);
        entity.setPosition(this.position);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setDescription(this.description);
        entity.setUpdatedBy(updatedBy);
    }
}
