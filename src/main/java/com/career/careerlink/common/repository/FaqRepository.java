package com.career.careerlink.common.repository;

import com.career.careerlink.common.entity.Faq;
import com.career.careerlink.common.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByCategory(Category category);
}
