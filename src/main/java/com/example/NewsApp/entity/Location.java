package com.example.NewsApp.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private String zipCode;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
