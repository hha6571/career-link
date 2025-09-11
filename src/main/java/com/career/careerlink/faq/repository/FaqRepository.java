package com.career.careerlink.faq.repository;

import com.career.careerlink.faq.entity.Faq;
import com.career.careerlink.faq.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByCategory(Category category);
}
