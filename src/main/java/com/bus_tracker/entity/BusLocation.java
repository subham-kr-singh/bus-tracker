package com.bus_tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BusLocation {
    @Id
    private Long id;
}
