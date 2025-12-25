package com.bus_tracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class CommuteStatusResponse {
    private String direction; // "INCOMING" or "OUTGOING"
    private boolean showDestinationSelector;
    private StopDto nearestStop;
    private List<TodayScheduleDto> availableBuses;
    private double distanceFromCollegeMeters;
    private String assumedReason; // "OUTSIDE_COLLEGE_GEOFENCE" or "INSIDE_COLLEGE_GEOFENCE"
}
