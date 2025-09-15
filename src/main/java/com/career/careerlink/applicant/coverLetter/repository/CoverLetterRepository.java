package com.career.careerlink.applicant.coverLetter.repository;

import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, Integer> {
    List<CoverLetter> findByUserIdOrderByUpdatedAtDesc(String userId);
    Optional<CoverLetter> findByCoverLetterIdAndUserId(Integer coverLetterId, String userId);
}
