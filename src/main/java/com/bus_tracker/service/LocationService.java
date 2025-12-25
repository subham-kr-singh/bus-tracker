package com.bus_tracker.service;

import com.bus_tracker.entity.BusLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class LocationService {

    // Helper map to store live locations in memory (RAM)
    // BusID -> BusLocation Object
    private final Map<Long, BusLocation> locationStore = new ConcurrentHashMap<>();

    public BusLocation updateLocation(Long busId, Double lat, Double lng, Double speed) {
        if (busId == null || lat == null || lng == null) {
            throw new IllegalArgumentException("busId, latitude, and longitude cannot be null");
        }

        BusLocation location = new BusLocation();
        location.setBusId(busId);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setSpeed(speed);
        location.setLastUpdatedAt(LocalDateTime.now());

        // Update in-memory store
        locationStore.put(busId, location);

        // Log occasionally for debugging (optional)
        log.debug("Updated location for Bus {}: {}, {}", busId, lat, lng);

        return location;
    }

    public BusLocation getLatestLocation(Long busId) {
        if (busId == null) {
            return null;
        }
        return locationStore.get(busId);
    }

    public List<String> findNearbyBusIds(Double lat, Double lng, double radiusMeters) {
        // Simple linear scan in memory - very fast for small fleets (< 1000 buses)
        return locationStore.values().stream()
                .filter(loc -> calculateDistance(lat, lng, loc.getLatitude(), loc.getLongitude()) <= radiusMeters)
                .map(loc -> loc.getBusId().toString())
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance between two points in meters
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
