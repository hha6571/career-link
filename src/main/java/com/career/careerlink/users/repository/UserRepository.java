package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.applicants;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<applicants, Long> {
    boolean existsByLoginId(String loginId);
    Optional<applicants> findByLoginId(String loginId);
}
