package com.bus_tracker.dto;

import lombok.Data;

@Data
public class StopDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double distance;
}
