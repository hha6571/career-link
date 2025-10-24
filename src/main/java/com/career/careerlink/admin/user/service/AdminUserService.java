package com.career.careerlink.admin.user.service;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.AdminEmployersSearchRequest;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminUserService {
    Page<AdminEmployerRequestDto> getEmployers(AdminEmployersSearchRequest searchRequest);
    void approveEmployer(String employerId,String adminUserId);
    int approveEmployerBulk(@RequestBody List<String> targetEmployerIds, String adminUserId);
    Page<UsersDto> getUsers(UsersRequestDto req);
    void saveUsers(@RequestBody List<UsersDto> list);
}
