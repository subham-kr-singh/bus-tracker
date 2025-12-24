package com.bus_tracker.dto;

import lombok.Data;

@Data
public class TodayScheduleDto {
    private Long id;
    private String busNumber;
    private String routeName;
    private String direction;
}
