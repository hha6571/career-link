package com.career.careerlink.dashboard.service;

import com.career.careerlink.dashboard.dto.PointDto;
import com.career.careerlink.dashboard.entity.enums.Granularity;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    List<PointDto> getPostingStats(Granularity granularity, LocalDate from, LocalDate to, String employerUserId);
    List<PointDto> getApplicationStats(Granularity granularity, LocalDate from, LocalDate to, String employerUserId);
}
