package com.career.careerlink.admin.service;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.common.response.ServiceResult;

import java.util.List;

public interface AdminService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
    ServiceResult getAllMenus();
    ServiceResult saveMenus(List<MenuDto> menuDtos);
}