package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Applicant, String> {
    boolean existsByLoginId(String loginId);
}
