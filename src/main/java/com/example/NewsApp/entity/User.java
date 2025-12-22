package com.example.NewsApp.entity;

import com.example.NewsApp.dto.AuthProvider;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String mobileNumber;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;


    private boolean reporterApproved;

    private LocalDateTime registeredAt;

    private LocalDateTime approvedAt;

    // used to remove location details form location table
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;

   // used to delete role form role table
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<UserRole> userRoles;

    private boolean enabled;
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<News> newsList;

    @Column(nullable = false)
    private boolean profileCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // LOCAL, GOOGLE



}
