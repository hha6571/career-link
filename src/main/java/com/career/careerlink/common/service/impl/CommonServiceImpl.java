package com.career.careerlink.common.service.impl;

import com.career.careerlink.admin.dto.CommonCodeDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.admin.entity.Menu;
import com.career.careerlink.admin.mapper.CommonCodeMapper;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.career.careerlink.admin.repository.MenuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MenuRepository menuRepository;
    private final CommonCodeMapper commonCodeMapper;

    @Override
    public List<MenuDto> getAllMenus(String accessToken) {
        String userRole = jwtTokenProvider.getRole(accessToken.replace("Bearer ",""));
        List<Menu> menus = menuRepository.findByAccessRoleOrderByDisplayOrderAscMenuIdAsc(userRole);
        return MenuDto.listOf(menus);
    }

    @Override
    public List<CommonCodeDto> getCommonCodes(String groupCode) {
        return commonCodeMapper.getCommonCodes(groupCode);
    }
}
