package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.CoverLetter;
import com.career.careerlink.applicant.entity.Resume;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverLetterDto {
    private Integer coverLetterId;
    private String title;
    private String content;

    public static CoverLetterDto of(CoverLetter entity) {
        return CoverLetterDto.builder()
                .coverLetterId(entity.getCoverLetterId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }

    public static List<CoverLetterDto> listOf(List<CoverLetter> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(CoverLetterDto::of).collect(Collectors.toList());
    }

    public CoverLetter toEntity(Resume resume, String createdBy) {
        return CoverLetter.builder()
                .coverLetterId(this.coverLetterId)
                .resume(resume)
                .title(this.title)
                .content(this.content)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    public void updateEntity(CoverLetter entity, String updatedBy) {
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setUpdatedBy(updatedBy);
    }
}
