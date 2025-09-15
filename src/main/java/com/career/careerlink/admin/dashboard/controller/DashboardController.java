package com.career.careerlink.admin.dashboard.controller;

import com.career.careerlink.admin.commonCode.service.CommonCodeService;
import com.career.careerlink.admin.dashboard.dto.PointDto;
import com.career.careerlink.admin.dashboard.entity.enums.Granularity;
import com.career.careerlink.admin.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * (마이페이지) 관리자 대시보드 - 등록 공고 수
     */
    @GetMapping("/stats/postings")
    public List<PointDto> postingStats(
            @RequestParam Granularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return dashboardService.getPostingStats(granularity, from, to);
    }
}


