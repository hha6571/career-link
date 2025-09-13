package com.career.careerlink.applicant.repository;

import com.career.careerlink.applicant.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, String> {
    Optional<Applicant> findByUserId(String userId);
    @Modifying(clearAutomatically = true)
    @Query("""
      update ApplicantInfo a
         set a.userStatus = 'ACTIVE',
             a.dormantAt = null,
             a.updatedAt = CURRENT_TIMESTAMP
       where a.loginId = :loginId
    """)
    int reactivateByLoginId(@Param("loginId") String loginId);
}