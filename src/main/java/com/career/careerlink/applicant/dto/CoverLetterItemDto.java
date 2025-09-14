package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.CoverLetter;
import com.career.careerlink.applicant.entity.CoverLetterItem;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverLetterItemDto {
    private Integer itemId;
    private String title;
    private String content;

    public static CoverLetterItemDto of(CoverLetterItem entity) {
        return CoverLetterItemDto.builder()
                .itemId(entity.getItemId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }

    public static List<CoverLetterItemDto> listOf(List<CoverLetterItem> entities) {
        return entities.stream().map(CoverLetterItemDto::of).collect(Collectors.toList());
    }

    public CoverLetterItem toEntity(CoverLetter parent, String userId) {
        return CoverLetterItem.builder()
                .itemId(this.itemId)
                .coverLetter(parent)
                .title(this.title)
                .content(this.content)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    public void updateEntity(CoverLetterItem entity, String userId) {
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setUpdatedBy(userId);
    }
}
