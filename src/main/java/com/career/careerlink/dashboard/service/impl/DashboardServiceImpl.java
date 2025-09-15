package com.career.careerlink.dashboard.service.impl;

import com.career.careerlink.dashboard.dto.PointDto;
import com.career.careerlink.dashboard.entity.enums.Granularity;
import com.career.careerlink.dashboard.mapper.StatsMapper;
import com.career.careerlink.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StatsMapper statsMapper;

    public List<PointDto> getPostingStats(Granularity g, LocalDate from, LocalDate to, String employerUserId) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toEx = switch (g) {
            case DAY   -> to.plusDays(1).atStartOfDay();
            case MONTH -> to.withDayOfMonth(1).plusMonths(1).atStartOfDay();
            case YEAR  -> to.withDayOfYear(1).plusYears(1).atStartOfDay();
        };

        String pattern = switch (g) {
            case DAY   -> "%Y-%m-%d";
            case MONTH -> "%Y-%m";
            case YEAR  -> "%Y";
        };

        return statsMapper.countPostings(g, fromDt, toEx, pattern, employerUserId);
    }

    public List<PointDto> getApplicationStats(Granularity g, LocalDate from, LocalDate to, String employerUserId) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toEx = switch (g) {
            case DAY   -> to.plusDays(1).atStartOfDay();
            case MONTH -> to.withDayOfMonth(1).plusMonths(1).atStartOfDay();
            case YEAR  -> to.withDayOfYear(1).plusYears(1).atStartOfDay();
        };

        String pattern = switch (g) {
            case DAY   -> "%Y-%m-%d";
            case MONTH -> "%Y-%m";
            case YEAR  -> "%Y";
        };

        return statsMapper.countApplicants(g, fromDt, toEx, pattern, employerUserId);
    }
}
