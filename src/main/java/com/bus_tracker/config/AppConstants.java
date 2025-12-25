package com.bus_tracker.config;

public class AppConstants {
    // Oriental College, Bhopal Coordinates
    public static final double COLLEGE_LAT = 23.259933;
    public static final double COLLEGE_LNG = 77.412615;

    // Radius in meters to consider "Inside College"
    public static final double COLLEGE_RADIUS_METERS = 500.0;

    // Buffer to avoid flickering at the edge
    public static final double GEOFENCE_BUFFER_METERS = 30.0;
}
