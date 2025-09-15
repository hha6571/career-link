package com.career.careerlink.admin.menu.controller;

import com.career.careerlink.admin.menu.dto.MenuDto;
import com.career.careerlink.admin.menu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    /**메뉴관리**/
    @GetMapping("/menu")
    public List<MenuDto> getAllMenus(@RequestParam String accessRole) {
        return adminMenuService.getAllMenus(accessRole);
    }

    @PostMapping("/saveMenus")
    public void saveMenus(@RequestBody MenuDto saveDto) {
        adminMenuService.saveMenus(saveDto);
    }
}
