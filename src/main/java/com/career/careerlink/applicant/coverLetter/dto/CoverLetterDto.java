package com.career.careerlink.applicant.coverLetter.dto;

import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import com.career.careerlink.common.enums.YnType;
import lombok.*;

import java.time.LocalDateTime;
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
    private String userId;
    private String coverLetterTitle;
    private YnType isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CoverLetterItemDto> items;

    /** 단건조회 (items 포함) */
    public static CoverLetterDto of(CoverLetter entity) {
        return CoverLetterDto.builder()
                .coverLetterId(entity.getCoverLetterId())
                .userId(entity.getUserId())
                .coverLetterTitle(entity.getCoverLetterTitle())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(CoverLetterItemDto.listOf(entity.getItems()))
                .build();
    }

    /** 전체조회 (items 빈 리스트) */
    public static List<CoverLetterDto> listOf(List<CoverLetter> entities) {
        return entities.stream()
                .map(e -> CoverLetterDto.builder()
                        .coverLetterId(e.getCoverLetterId())
                        .userId(e.getUserId())
                        .coverLetterTitle(e.getCoverLetterTitle())
                        .isActive(e.getIsActive())
                        .createdAt(e.getCreatedAt())
                        .updatedAt(e.getUpdatedAt())
                        .items(Collections.emptyList())
                        .build())
                .collect(Collectors.toList());
    }

    /** 최초 생성 */
    public CoverLetter toEntity(String userId) {
        CoverLetter coverLetter = CoverLetter.builder()
                .coverLetterId(this.coverLetterId)
                .userId(userId) // ✅ 프론트에서 안 받고 서비스에서 세팅
                .coverLetterTitle(this.coverLetterTitle)
                .isActive(this.isActive)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        if (this.items != null) {
            this.items.forEach(itemDto -> coverLetter.addItem(itemDto.toEntity(coverLetter, userId)));
        }
        return coverLetter;
    }

    /** 업데이트 */
    public void updateEntity(CoverLetter entity, String userId) {
        entity.setCoverLetterTitle(this.coverLetterTitle);
        entity.setIsActive(this.isActive);
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());

        if (this.items != null) {
            if (entity.getItems() == null) {
                entity.setItems(new java.util.ArrayList<>()); // ✅ 안전하게 초기화
            }

            List<Integer> incomingIds = this.items.stream()
                    .map(CoverLetterItemDto::getItemId)
                    .toList();

            entity.getItems().removeIf(item -> !incomingIds.contains(item.getItemId()));

            for (CoverLetterItemDto itemDto : this.items) {
                if (itemDto.getItemId() == null) {
                    entity.addItem(itemDto.toEntity(entity, userId));
                } else {
                    entity.getItems().stream()
                            .filter(i -> i.getItemId().equals(itemDto.getItemId()))
                            .findFirst()
                            .ifPresent(i -> itemDto.updateEntity(i, userId));
                }
            }
        }
    }
}
