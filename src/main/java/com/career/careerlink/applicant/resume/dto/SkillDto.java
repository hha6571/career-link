package com.career.careerlink.applicant.resume.dto;

import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.applicant.resume.entity.Skill;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDto {
    private Integer skillId;
    private String skillName;
    private String proficiency;

    public static SkillDto of(Skill entity) {
        return SkillDto.builder()
                .skillId(entity.getSkillId())
                .skillName(entity.getSkillName())
                .proficiency(entity.getProficiency())
                .build();
    }

    public static List<SkillDto> listOf(Collection<Skill> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(SkillDto::of).collect(Collectors.toList());
    }

    public Skill toEntity(Resume resume, String createdBy) {
        return Skill.builder()
                .skillId(this.skillId)
                .resume(resume)
                .skillName(this.skillName)
                .proficiency(this.proficiency)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(Skill entity, String updatedBy) {
        entity.setSkillName(this.skillName);
        entity.setProficiency(this.proficiency);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
