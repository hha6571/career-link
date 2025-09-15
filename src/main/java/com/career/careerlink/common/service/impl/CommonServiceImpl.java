package com.career.careerlink.common.service.impl;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.menu.dto.MenuDto;
import com.career.careerlink.admin.menu.entity.Menu;
import com.career.careerlink.admin.commonCode.mapper.CommonCodeMapper;
import com.career.careerlink.admin.menu.repository.MenuRepository;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        List<Menu> menus = menuRepository.findByAccessRoleAndIsActiveOrderByDisplayOrderAscMenuIdAsc(userRole,"Y");
        return MenuDto.listOf(menus);
    }

    @Override
    public List<MenuDto> getAllMenusByPublic(String accessRole) {
        List<Menu> menus = menuRepository.findByAccessRoleOrderByDisplayOrderAscMenuIdAsc(accessRole);
        return MenuDto.listOf(menus);
    }

    @Override
    public List<CommonCodeDto> getCommonCodes(String groupCode) {
        return commonCodeMapper.getCommonCodes(groupCode);
    }

    @Override
    public List<CommonCodeDto> allCodesByGroup(String groupCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        return commonCodeMapper.allCodesByGroup(groupCode);
    }

    @Override
    public List<CommonCodeDto> parentsByGroup(String groupCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        return commonCodeMapper.parentsByGroup(groupCode);
    }

    @Override
    public List<CommonCodeDto> childrenByParent(String groupCode, String parentCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        if (!StringUtils.hasText(parentCode)) {
            throw new IllegalArgumentException("parentCode is required");
        }
        return commonCodeMapper.childrenByParent(groupCode, parentCode);
    }
}
