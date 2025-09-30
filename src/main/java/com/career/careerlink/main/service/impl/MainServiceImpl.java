package com.career.careerlink.main.service.impl;

import com.career.careerlink.jobScrap.repository.JobPostingScrapRepository;
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
    private final JobPostingScrapRepository jobPostingScrapRepository;

    /**
     * 메인화면 공고목록
     */
    public MainJobsDtos.MainJobsResponse getMainJobs(String userId) {
        List<Map<String, Object>> rows = mainMapper.selectMainJobs();
        var items = rows.stream()
                .map(m -> {
                    boolean scrapped = false;
                    if (userId != null && !userId.isBlank()) {
                        scrapped = jobPostingScrapRepository
                                .existsByUserIdAndJobPosting_JobPostingId(userId, ((Number) m.get("jobId")).intValue());
                    }
                    return MainJobsDtos.MainJobsItem.fromMap(m, scrapped);
                })
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
