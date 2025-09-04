package com.career.careerlink.admin.service;

import com.career.careerlink.admin.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
    List<MenuDto> getAllMenus(String accessRole);
    List<CommonCodeDto> getCommonCodes(String groupCode);
    Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req);
    Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req);
    void saveMenus(MenuDto saveDto);
    void saveCommonCodes(CommonCodeSaveDto saveDto);
}
