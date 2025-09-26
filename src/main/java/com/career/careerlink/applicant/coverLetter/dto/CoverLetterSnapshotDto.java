package com.career.careerlink.applicant.coverLetter.dto;

import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverLetterSnapshotDto {
    private Integer coverLetterId;
    private String coverLetterTitle;
    private List<CoverLetterItemDto> items;

    /** Entity → DTO 변환 */
    public static CoverLetterSnapshotDto of(CoverLetter entity) {
        if (entity == null) return null;

        return CoverLetterSnapshotDto.builder()
                .coverLetterId(entity.getCoverLetterId())
                .coverLetterTitle(entity.getCoverLetterTitle())
                .items(entity.getItems() != null
                        ? entity.getItems().stream()
                        .map(CoverLetterItemDto::of)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
