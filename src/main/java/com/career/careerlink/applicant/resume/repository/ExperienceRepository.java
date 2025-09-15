package com.career.careerlink.applicant.resume.repository;

import com.career.careerlink.applicant.resume.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
}
