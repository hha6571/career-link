package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Application;
import com.career.careerlink.applicant.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Page<Application> findByUserId(String userId, Pageable pageable);
    Page<Application> findByUserIdAndAppliedAtAfter(String userId, LocalDateTime appliedAt, Pageable pageable);
    List<Application> findByJobPosting_JobPostingId(Integer jobPostingId);
    boolean existsByUserIdAndJobPosting_JobPostingIdAndStatusNot(String userId, Integer jobPostingId, ApplicationStatus status);

}
