package com.career.careerlink.admin.service;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;

import java.util.List;

public interface AdminService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
}