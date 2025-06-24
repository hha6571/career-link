package com.career.careerlink.faq.dto;

import com.career.careerlink.faq.entity.Faq;
import com.career.careerlink.faq.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Entity → DTO 변환
    public static FaqDto of(Faq faq) {
        return FaqDto.builder()
                .faqId(faq.getFaqId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .createdBy(faq.getCreatedBy())
                .updatedBy(faq.getUpdatedBy())
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
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
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
        return authentication.getName();
    }
}
