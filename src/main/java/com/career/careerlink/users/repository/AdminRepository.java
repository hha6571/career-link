package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    @Modifying(clearAutomatically = true)
    @Query("""
      update Admin ad
         set ad.adminStatus = 'ACTIVE',
             ad.updatedAt = CURRENT_TIMESTAMP
       where ad.loginId = :loginId
    """)
    int reactivateByLoginId(@Param("loginId") String loginId);
}