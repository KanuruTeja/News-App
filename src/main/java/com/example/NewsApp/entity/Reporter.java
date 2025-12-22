package com.example.NewsApp.entity;

import com.example.NewsApp.enums.ReporterStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reporters")
public class Reporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReporterStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime approvedAt;

    // Automatically set values when inserting
    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReporterStatus.PENDING;
        }
    }
}
