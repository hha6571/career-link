package com.career.careerlink.faq.service.impl;

import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.Faq;
import com.career.careerlink.faq.entity.enums.Category;
import com.career.careerlink.faq.repository.FaqRepository;
import com.career.careerlink.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqReposotory;

    @Override
    public List<FaqDto> getFaqs(Category category) {
        List<Faq> faqs = (category == null)
                ? faqReposotory.findAll()
                : faqReposotory.findByCategory(category);
        return FaqDto.listOf(faqs);
    }
}
