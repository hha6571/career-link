package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Application;
import com.career.careerlink.applicant.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByUserId(String userId);
    List<Application> findByJobPosting_JobPostingId(Integer jobPostingId);
    boolean existsByUserIdAndJobPosting_JobPostingIdAndStatusNot(String userId, Integer jobPostingId, ApplicationStatus status);

}
