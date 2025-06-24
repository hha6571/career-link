package com.career.careerlink.common.service;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.menu.dto.MenuDto;

import java.util.List;

public interface CommonService {
    List<MenuDto> getAllMenus();
    List<MenuDto> getAllMenusByPublic(String accessRole);
    List<CommonCodeDto> getCommonCodes(String groupCode);
    List<CommonCodeDto> allCodesByGroup(String groupCode);
    List<CommonCodeDto> parentsByGroup(String groupCode);
    List<CommonCodeDto> childrenByParent(String groupCode, String parentCode);
    }
