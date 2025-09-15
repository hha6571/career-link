package com.career.careerlink.applicant.resume.repository;

import com.career.careerlink.applicant.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findByUserId(String userId);
    Optional<Resume> findByResumeIdAndUserId(Integer resumeId, String userId);
}
