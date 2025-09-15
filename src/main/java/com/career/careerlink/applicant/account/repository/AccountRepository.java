package com.career.careerlink.applicant.account.repository;

import com.career.careerlink.users.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Applicant, String> {
}