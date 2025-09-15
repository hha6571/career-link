package com.career.careerlink.common.service;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.menu.dto.MenuDto;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface CommonService {
    List<MenuDto> getAllMenus(@RequestHeader("Authorization") String accessToken);
    List<MenuDto> getAllMenusByPublic(String accessRole);
    List<CommonCodeDto> getCommonCodes(String groupCode);
    List<CommonCodeDto> allCodesByGroup(String groupCode);
    List<CommonCodeDto> parentsByGroup(String groupCode);
    List<CommonCodeDto> childrenByParent(String groupCode, String parentCode);
    }
