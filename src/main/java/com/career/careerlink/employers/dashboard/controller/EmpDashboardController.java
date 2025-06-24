package com.career.careerlink.employers.dashboard.controller;

import com.career.careerlink.dashboard.dto.PointDto;
import com.career.careerlink.dashboard.entity.enums.Granularity;
import com.career.careerlink.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/emp/dashboard")
@RequiredArgsConstructor
public class EmpDashboardController {

    private final DashboardService dashboardService;

    /**
     * (마이페이지) 기업사용자 대시보드 - 등록 공고 수
     */
    @GetMapping("/stats/postings")
    public List<PointDto> postingStats(
            @RequestParam Granularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal
    ) {
        String employerUserId = principal.getName();
        return dashboardService.getPostingStats(granularity, from, to, employerUserId);
    }

    /**
     * (마이페이지) 기업사용자 대시보드 - 지원 수
     */
    @GetMapping("/stats/applicants")
    public List<PointDto> applicationStats(
            @RequestParam Granularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal
    ) {
        String employerUserId = principal.getName();
        return dashboardService.getApplicationStats(granularity, from, to, employerUserId);
    }
}


