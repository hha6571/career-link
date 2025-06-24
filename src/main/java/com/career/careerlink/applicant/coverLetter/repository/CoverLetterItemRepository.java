package com.career.careerlink.applicant.coverLetter.repository;

import com.career.careerlink.applicant.coverLetter.entity.CoverLetterItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverLetterItemRepository extends JpaRepository<CoverLetterItem, Integer> {
}
