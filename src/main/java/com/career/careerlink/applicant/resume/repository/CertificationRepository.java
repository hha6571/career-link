package com.career.careerlink.applicant.resume.repository;

import com.career.careerlink.applicant.resume.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
}
