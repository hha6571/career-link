package com.career.careerlink.common.repository;

import com.career.careerlink.common.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeDetailRepository extends JpaRepository<Notice, Long> {
}
