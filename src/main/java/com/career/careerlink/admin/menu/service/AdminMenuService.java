package com.career.careerlink.admin.menu.service;

import com.career.careerlink.admin.menu.dto.MenuDto;

import java.util.List;

public interface AdminMenuService {
    List<MenuDto> getAllMenus(String accessRole);
    void saveMenus(MenuDto saveDto);
}
