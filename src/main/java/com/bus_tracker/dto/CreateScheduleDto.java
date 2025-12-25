package com.bus_tracker.dto;

import lombok.Data;

@Data
public class CreateScheduleDto {
    private Long routeId;
    private Long busId;
    private String direction;
}
