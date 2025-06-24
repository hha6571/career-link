package com.career.careerlink.jobScrap.service.impl;

import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.repository.JobRepository;
import com.career.careerlink.jobScrap.dto.JobPostingScrapDto;
import com.career.careerlink.jobScrap.entity.JobPostingScrap;
import com.career.careerlink.jobScrap.repository.JobPostingScrapRepository;
import com.career.careerlink.jobScrap.service.JobPostingScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostingScrapServiceImpl implements JobPostingScrapService {

    private final JobPostingScrapRepository jobPostingScrapRepository;
    private final JobRepository jobRepository;

    /**
     * 내가 스크랩한 공고 페이징 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingScrapDto> getMyScraps(String userId, Pageable pageable) {
        return jobPostingScrapRepository.findByUserId(userId, pageable)
                .map(JobPostingScrapDto::of);
    }

    /**
     * 스크랩 추가
     */
    @Override
    public void addScrap(String userId, Integer jobPostingId) {
        // 이미 스크랩했는지 확인
        if (jobPostingScrapRepository.existsByUserIdAndJobPosting_JobPostingId(userId, jobPostingId)) {
            throw new IllegalStateException("이미 스크랩한 공고입니다.");
        }

        // 공고 존재 여부 확인
        JobPosting jobPosting = jobRepository.findById(jobPostingId)
                .orElseThrow(() -> new CareerLinkException("채용공고를 찾을 수 없습니다."));

        // 엔티티 생성
        JobPostingScrap scrap = JobPostingScrap.builder()
                .userId(userId)
                .jobPosting(jobPosting)
                .createdBy(userId)
                .build();

        jobPostingScrapRepository.save(scrap);
    }

    /**
     * 스크랩 삭제
     */
    @Override
    public void removeScrap(String userId, Integer jobPostingId) {
        jobPostingScrapRepository.deleteByUserIdAndJobPosting_JobPostingId(userId, jobPostingId);
    }

}
