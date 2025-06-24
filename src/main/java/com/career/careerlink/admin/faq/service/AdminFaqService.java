package com.career.careerlink.admin.faq.service;

import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.enums.Category;

import java.util.List;

public interface AdminFaqService {
    List<FaqDto> getFaqs(Category category);
    void createFaq(FaqDto dto);
    void updateFaq(FaqDto dto);
    void deleteFaq(Long faqId);
}
