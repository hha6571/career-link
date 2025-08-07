package com.career.careerlink.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate  // 변경된 컬럼만 UPDATE
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", updatable = false, nullable = false)
    private Integer menuId;

    // 부모-자식 self-reference 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Menu> children = new ArrayList<>();

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "menu_path", length = 255)
    private String menuPath;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    // ENUM('Y','N') 대신 VARCHAR(1)로 매핑, DB에 enum 제약은 직접 관리
    @Column(name = "is_active", nullable = false, length = 1)
    private String isActive;

    @Column(name = "access_role", length = 20)
    private String accessRole;

    @Column(name = "icon", length = 20)
    private String icon;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
