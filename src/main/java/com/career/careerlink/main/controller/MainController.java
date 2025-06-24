package com.career.careerlink.main.controller;

import com.career.careerlink.main.dto.MainEmployersDtos;
import com.career.careerlink.main.dto.MainJobsDtos;
import com.career.careerlink.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
@Validated
public class MainController {

    private final MainService mainService;

    /**
     * 메인화면 인기공고 top12 조회
     */
    @GetMapping("/job-postings")
    public MainJobsDtos.MainJobsResponse getMainJobs(Principal principal) {
        String userId = (principal != null) ? principal.getName() : null;
        return mainService.getMainJobs(userId);
    }

    /**
     * 메인화면 기업정보 조회
     */
    @GetMapping("/employers")
    public MainEmployersDtos.MainEmployersResponse getMainEmployers() {
        return mainService.getMainEmployers();
    }
}
