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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "user_id",            // FK column in news_table
//            referencedColumnName = "id", // PK in users table
//            nullable = false
//    )
//@JsonBackReference
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
        name = "user_id",            // FK column in news_table
        referencedColumnName = "id", // PK in users table
        nullable = false
)
    @JsonBackReference
    private User createdBy;//repoter id


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = NewsStatus.PENDING;
    }

}
