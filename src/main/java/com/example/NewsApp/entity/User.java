package com.example.NewsApp.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.NewsApp.dto.AuthProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonIgnore
    private List<News> newsList;

    @Column(nullable = false)
    private boolean profileCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // LOCAL, GOOGLE
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private UserExperience userExperience;

  


}


