package com.career.careerlink.faq.controller;

import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.enums.Category;
import com.career.careerlink.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    /**자주하는질문**/
    @GetMapping("/getFaqs")
    public List<FaqDto> getFaqs(@RequestParam Category category) {
        return faqService.getFaqs(category);
    }
}
