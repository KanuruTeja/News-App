package com.example.NewsApp.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Role {

    @Id
    private Integer id; // 1=ADMIN, 2=REPORTER, 3=USER

    @Column(unique = true, nullable = false)
    private String name;
}


