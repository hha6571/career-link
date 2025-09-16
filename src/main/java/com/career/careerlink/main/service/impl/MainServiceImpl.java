package com.career.careerlink.main.service.impl;

import com.career.careerlink.main.dto.MainEmployersDtos;
import com.career.careerlink.main.dto.MainJobsDtos;
import com.career.careerlink.main.mapper.MainMapper;
import com.career.careerlink.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final MainMapper mainMapper;

    /**
     * 메인화면 공고목록
     */
    public MainJobsDtos.MainJobsResponse getMainJobs() {
        List<Map<String, Object>> rows = mainMapper.selectMainJobs();
        var items = rows.stream()
                .map(MainJobsDtos.MainJobsItem::fromMap)
                .toList();
        return new MainJobsDtos.MainJobsResponse(items);
    }

    /**
     * 메인화면 기업목록
     */
    public MainEmployersDtos.MainEmployersResponse getMainEmployers() {
        List<Map<String, Object>> rows = mainMapper.selectMainEmployers();
        var items = rows.stream()
                .map(MainEmployersDtos.MainEmployersItem::fromMap)
                .toList();
        return new MainEmployersDtos.MainEmployersResponse(items);
    }
}
