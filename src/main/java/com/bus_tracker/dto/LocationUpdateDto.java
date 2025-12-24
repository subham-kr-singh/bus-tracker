package com.bus_tracker.dto;

import lombok.Data;

@Data
public class LocationUpdateDto {
    private Long scheduleId;
    private Double latitude;
    private Double longitude;
    private Double speed;
}
