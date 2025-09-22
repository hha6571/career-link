package com.career.careerlink.faq.entity;

import com.career.careerlink.faq.entity.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "faqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Integer faqId;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "LONGTEXT")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String answer;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
