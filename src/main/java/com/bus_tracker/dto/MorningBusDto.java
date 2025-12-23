package com.bus_tracker.dto;

import lombok.Data;

@Data
public class MorningBusDto {
    private String busNumber;
    private int etaMinutes;
    private double latitude;
    private double longitude;
    private String routeName;
}
