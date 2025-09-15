package com.career.careerlink.admin.menu.dto;

import com.career.careerlink.admin.menu.entity.Menu;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {
    private Integer menuId;
    private Integer parentId;
    private String menuName;
    private String menuPath;
    private Integer level;
    private Integer displayOrder;
    private String isActive;
    private String accessRole;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MenuDto of(Menu menu) {
        return MenuDto.builder()
                .menuId(menu.getMenuId())
                .parentId(menu.getParent() != null ? menu.getParent().getMenuId() : null)
                .menuName(menu.getMenuName())
                .menuPath(menu.getMenuPath())
                .level(menu.getLevel())
                .displayOrder(menu.getDisplayOrder())
                .isActive(menu.getIsActive())
                .accessRole(menu.getAccessRole())
                .icon(menu.getIcon())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    public static List<MenuDto> listOf(List<Menu> menus) {
        return menus.stream()
                .map(MenuDto::of)
                .collect(Collectors.toList());
    }

    private List<MenuDto> inserts;
    private List<MenuDto> updates;
    private List<Integer> deletes;
}
