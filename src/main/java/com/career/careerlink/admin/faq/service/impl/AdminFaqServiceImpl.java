package com.career.careerlink.admin.faq.service.impl;

import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.Faq;
import com.career.careerlink.faq.entity.enums.Category;
import com.career.careerlink.faq.repository.FaqRepository;
import com.career.careerlink.admin.faq.service.AdminFaqService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFaqServiceImpl implements AdminFaqService {

    private final FaqRepository faqRepository;

    @Override
    public List<FaqDto> getFaqs(Category category) {
        List<Faq> faqs = faqRepository.findByCategory(category);
        return FaqDto.listOf(faqs);
    }

    @Override
    @Transactional
    public void createFaq(FaqDto dto) {
        Faq faq = dto.toEntity();
        faqRepository.save(faq);
    }

    @Override
    @Transactional
    public void updateFaq(FaqDto dto) {
        Faq faq = faqRepository.findById(dto.getFaqId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 FAQ입니다."));
        dto.updateEntity(faq);
    }

    @Override
    @Transactional
    public void deleteFaq(Long faqId) {
        faqRepository.deleteById(faqId);
    }

    private static <T> List<T> nvl(List<T> v) { return v == null ? List.of() : v; }
}
