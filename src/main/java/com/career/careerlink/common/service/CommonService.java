package com.career.careerlink.common.service;

import com.career.careerlink.admin.dto.CommonCodeDto;
import com.career.careerlink.admin.dto.MenuDto;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface CommonService {
    List<MenuDto> getAllMenus(@RequestHeader("Authorization") String accessToken);
    List<CommonCodeDto> getCommonCodes(String groupCode);
}
