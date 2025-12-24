package com.bus_tracker.dto;

import lombok.Data;

@Data
public class MorningBusDto {
    private Long busId;
    private String busNumber;
    private String routeName;
    private Integer etaMinutes;
    private Double latitude;
    private Double longitude;
}
