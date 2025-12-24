package com.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "buses")
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String busNumber;

    private Integer capacity;
    private String gpsDeviceId;
    private String status;
}
