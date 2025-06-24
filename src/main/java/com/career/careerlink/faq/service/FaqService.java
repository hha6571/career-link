package com.career.careerlink.faq.service;

import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.enums.Category;

import java.util.List;

public interface FaqService {
    List<FaqDto> getFaqs(Category category);
    }
