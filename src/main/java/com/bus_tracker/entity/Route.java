package com.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "routes")
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String direction;
}
