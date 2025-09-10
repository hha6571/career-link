package com.career.careerlink.common.dto;

import com.career.careerlink.common.entity.Faq;
import com.career.careerlink.common.entity.enums.Category;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqDto {
    private Integer faqId;
    private String question;
    private String answer;
    private Category category;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity → DTO 변환
    public static FaqDto of(Faq faq) {
        return FaqDto.builder()
                .faqId(faq.getFaqId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdBy(faq.getCreatedBy())
                .updatedBy(faq.getUpdatedBy())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }

    public static List<FaqDto> listOf(List<Faq> faq) {
        return faq.stream()
                .map(FaqDto::of)
                .collect(Collectors.toList());
    }

    // DTO → Entity 신규 등록용
    public Faq toEntity() {
        String userId = getCurrentUserId();
        return Faq.builder()
                .question(this.question)
                .answer(this.answer)
                .category(this.category)
                .createdBy(userId)
                .updatedBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DTO → Entity 수정용
    public void updateEntity(Faq faq) {
        String userId = getCurrentUserId();
        faq.setQuestion(this.question);
        faq.setAnswer(this.answer);
        faq.setCategory(this.category);
        faq.setUpdatedBy(userId);
        faq.setUpdatedAt(LocalDateTime.now());
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }
}
