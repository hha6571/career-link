package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
}
