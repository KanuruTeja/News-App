package com.example.NewsApp.entity;

import com.example.NewsApp.enums.CategoryType;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.enums.NewsType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "news_table")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Column owner
    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String headline;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private NewsType newsType;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    private NewsStatus status;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    private String district;

    //  Read-only relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @JsonBackReference
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = NewsStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
