package com.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "stops")
@Data
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    private String description;
}
