package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
}
