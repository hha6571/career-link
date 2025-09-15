package com.career.careerlink.admin.dashboard.service;

import com.career.careerlink.admin.dashboard.dto.PointDto;
import com.career.careerlink.admin.dashboard.entity.enums.Granularity;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    List<PointDto> getPostingStats(Granularity granularity, LocalDate from, LocalDate to);
}
