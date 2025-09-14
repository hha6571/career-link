package com.career.careerlink.applicant.entity;

import com.career.careerlink.applicant.entity.enums.YnType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cover_letter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer coverLetterId;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 200)
    private String coverLetterTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private YnType isActive;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** ✅ 항상 초기화해두기 */
    @OneToMany(mappedBy = "coverLetter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoverLetterItem> items = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** 연관관계 메서드 */
    public void addItem(CoverLetterItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setCoverLetter(this);
    }
}
