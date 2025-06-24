package com.career.careerlink.admin.faq.controller;

import com.career.careerlink.admin.faq.service.AdminFaqService;
import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.faq.entity.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/faq")
@RequiredArgsConstructor
public class AdminFaqController {

    private final AdminFaqService faqService;

    /**
     * 자주하는질문
     **/
    @GetMapping("/getFaqs")
    public List<FaqDto> getFaqs(@RequestParam Category category) {
        return faqService.getFaqs(category);
    }

    @PostMapping("/createFaq")
    public void createFaq(@RequestBody FaqDto dto) {
        faqService.createFaq(dto);
    }

    @PutMapping("/updateFaq")
    public void updateFaq(@RequestBody FaqDto dto) {
        faqService.updateFaq(dto);
    }

    @DeleteMapping("/deleteFaq/{faqId}")
    public void deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
    }
}
