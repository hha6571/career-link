package com.career.careerlink.admin.user.service;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminUserService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
    Page<UsersDto> getUsers(UsersRequestDto req);
    void saveUsers(@RequestBody List<UsersDto> list);
}
