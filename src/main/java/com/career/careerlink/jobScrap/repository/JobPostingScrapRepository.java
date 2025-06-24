package com.career.careerlink.jobScrap.repository;

import com.career.careerlink.jobScrap.entity.JobPostingScrap;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingScrapRepository extends JpaRepository<JobPostingScrap, Integer> {
    boolean existsByUserIdAndJobPosting_JobPostingId(String userId, Integer jobPostingId);
    void deleteByUserIdAndJobPosting_JobPostingId(String userId, Integer jobPostingId);
    @EntityGraph(attributePaths = {"jobPosting", "jobPosting.employer"})
    Page<JobPostingScrap> findByUserId(@Param("userId") String userId, Pageable pageable);

}