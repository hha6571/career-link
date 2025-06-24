package com.career.careerlink.notice.repository;

import com.career.careerlink.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeDetailRepository extends JpaRepository<Notice, Integer> {
}
