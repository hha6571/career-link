package com.career.careerlink.admin.menu.service.impl;

import com.career.careerlink.admin.menu.dto.MenuDto;
import com.career.careerlink.admin.menu.entity.Menu;
import com.career.careerlink.admin.menu.repository.MenuRepository;
import com.career.careerlink.admin.menu.service.AdminMenuService;
import com.career.careerlink.global.exception.CareerLinkException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMenuServiceImpl implements AdminMenuService {

    private final MenuRepository menuRepository;

    private static <T> List<T> nvl(List<T> v) { return v == null ? List.of() : v; }

    /**메뉴관리**/
    @Override
    public List<MenuDto> getAllMenus(String accessRole) {
        List<Menu> menus = menuRepository.findByAccessRoleOrderByDisplayOrderAscMenuIdAsc(accessRole);
        return MenuDto.listOf(menus);
    }

    @Override
    @Transactional
    public void saveMenus(MenuDto saveDto) {
        var deletes = nvl(saveDto.getDeletes());
        var inserts = nvl(saveDto.getInserts());
        var updates = nvl(saveDto.getUpdates());
        if (!deletes.isEmpty()) {
            menuRepository.deleteAllByIdInBatch(deletes);
        }

        // 2) 삽입
        for (MenuDto dto : inserts) {
            if (Integer.valueOf(2).equals(dto.getLevel()) && dto.getParentId() == null) {
                throw new CareerLinkException("하위 메뉴 저장 시 parentId는 필수입니다.");
            }

            Menu menu = new Menu();
            menu.setMenuName(dto.getMenuName());
            menu.setMenuPath(dto.getMenuPath());
            menu.setDisplayOrder(dto.getDisplayOrder());
            if (dto.getParentId() == null) {
                menu.setLevel(1);
                menu.setParent(null);
            } else {
                Menu parent = menuRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new CareerLinkException("부모 메뉴 없음: " + dto.getParentId()));
                menu.setParent(parent);
                menu.setLevel(2); // 혹은 parent.getLevel() + 1;
            }
            menu.setIsActive(dto.getIsActive());
            menu.setAccessRole(dto.getAccessRole());
            menu.setIcon(dto.getIcon());

            menuRepository.save(menu);
        }

        // 3) 수정
        for (MenuDto dto : updates) {
            Menu menu = menuRepository.findById(dto.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("수정 대상 메뉴 없음: " + dto.getMenuId()));

            menu.setMenuName(dto.getMenuName());
            menu.setMenuPath(dto.getMenuPath());
            menu.setDisplayOrder(dto.getDisplayOrder());
            if (dto.getParentId() == null) {
                menu.setParent(null);
                menu.setLevel(1);
            } else {
                Menu parent = menuRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("부모 메뉴 없음: " + dto.getParentId()));
                menu.setParent(parent);
                menu.setLevel(2);
            }
            menu.setIsActive(dto.getIsActive());
            menu.setAccessRole(dto.getAccessRole());
            menu.setIcon(dto.getIcon());
        }
    }
}
