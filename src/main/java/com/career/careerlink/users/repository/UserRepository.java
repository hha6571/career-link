package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Applicant, String> {
    Optional<Applicant> findByUserId(String userId);
}
