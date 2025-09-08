package com.career.careerlink.admin.service;

import com.career.careerlink.admin.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
    List<MenuDto> getAllMenus(String accessRole);
    Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req);
    Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req);
    void saveMenus(MenuDto saveDto);
    void saveCommonCodes(CommonCodeSaveDto saveDto);
    Page<UsersDto> getUsers(UsersRequestDto req);
    void saveUsers(@RequestBody List<UsersDto> list);
}
