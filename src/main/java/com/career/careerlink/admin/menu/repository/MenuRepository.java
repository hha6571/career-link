package com.career.careerlink.admin.menu.repository;

import com.career.careerlink.admin.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByAccessRoleOrderByDisplayOrderAscMenuIdAsc(String accessRole);
    List<Menu> findByAccessRoleAndIsActiveOrderByDisplayOrderAscMenuIdAsc(String accessRole,String isActive);
}
