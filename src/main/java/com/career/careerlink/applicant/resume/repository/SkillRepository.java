package com.career.careerlink.applicant.resume.repository;

import com.career.careerlink.applicant.resume.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
}
