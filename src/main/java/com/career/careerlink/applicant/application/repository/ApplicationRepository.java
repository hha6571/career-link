package com.career.careerlink.applicant.application.repository;

import com.career.careerlink.applicant.application.entity.Application;
import com.career.careerlink.applicant.application.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Page<Application> findByUserId(String userId, Pageable pageable);
    Page<Application> findByUserIdAndAppliedAtAfter(String userId, LocalDateTime appliedAt, Pageable pageable);
    boolean existsByUserIdAndJobPosting_JobPostingIdAndStatusNot(String userId, Integer jobPostingId, ApplicationStatus status);

}
