package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Applicant, String> {
    Optional<Applicant> findByUserId(String userId);
    Optional<Applicant> findByEmail(String email);
    Optional<Applicant> findByUserNameAndEmail(String userName, String email);
    Optional<Applicant> findByUserNameAndEmailAndLoginId(String userName, String email, String loginId);
}
