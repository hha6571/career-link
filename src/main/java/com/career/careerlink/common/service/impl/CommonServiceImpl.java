package com.career.careerlink.common.service.impl;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.commonCode.mapper.CommonCodeMapper;
import com.career.careerlink.admin.menu.dto.MenuDto;
import com.career.careerlink.admin.menu.entity.Menu;
import com.career.careerlink.admin.menu.repository.MenuRepository;
import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.global.exception.CareerLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final MenuRepository menuRepository;
    private final CommonCodeMapper commonCodeMapper;

    @Override
    public List<MenuDto> getAllMenus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CareerLinkException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        String userRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)      // e.g. ROLE_USER
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a) // USER
                .findFirst()
                .orElseThrow(() -> new CareerLinkException(ErrorCode.UNAUTHORIZED, "권한이 없습니다."));
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
