package com.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "bus_locations")
@Data
public class BusLocation {
    @Id
    private Long busId;

    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private LocalDateTime lastUpdatedAt;
}
