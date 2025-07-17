package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    boolean existsByLoginId(String loginId);
    Optional<Applicant> findByLoginId(String loginId);
}
