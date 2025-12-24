package com.bus_tracker.service;

import com.bus_tracker.entity.BusLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class LocationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final java.util.Map<Long, BusLocation> memoryFallback = new java.util.concurrent.ConcurrentHashMap<>();

    private static final String LOCATION_GAME_KEY = "bus:location:%d";
    private static final String GEO_KEY = "buses:geo";

    public BusLocation updateLocation(Long busId, Double lat, Double lng, Double speed) {
        if (busId == null || lat == null || lng == null) {
            throw new IllegalArgumentException("busId, latitude, and longitude cannot be null");
        }

        BusLocation location = new BusLocation();
        location.setBusId(busId);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setSpeed(speed);
        location.setLastUpdatedAt(java.time.LocalDateTime.now());

        // Always update memory fallback for safety
        memoryFallback.put(busId, location);

        try {
            // 1. Store detailed info (Value operation)
            String key = String.format(LOCATION_GAME_KEY, busId);
            redisTemplate.opsForValue().set(key, location, 15, TimeUnit.MINUTES);

            // 2. Store Geospatial data (Geo operation)
            String busIdStr = busId.toString();
            redisTemplate.opsForGeo().add(GEO_KEY, new Point(lng, lat), busIdStr);
        } catch (Exception e) {
            log.error("Redis error in updateLocation, using memory fallback: {}", e.getMessage());
        }

        return location;
    }

    public BusLocation getLatestLocation(Long busId) {
        if (busId == null) {
            return null;
        }

        try {
            Object result = redisTemplate.opsForValue().get(String.format(LOCATION_GAME_KEY, busId));
            if (result instanceof BusLocation) {
                return (BusLocation) result;
            }
        } catch (Exception e) {
            log.error("Redis error in getLatestLocation for bus {}, using memory: {}", busId, e.getMessage());
        }

        return memoryFallback.get(busId);
    }

    public List<String> findNearbyBusIds(Double lat, Double lng, double radiusMeters) {
        try {
            Circle circle = new Circle(new Point(lng, lat), new Distance(radiusMeters, DistanceUnit.METERS));
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                    .includeDistance();

            var results = redisTemplate.opsForGeo().radius(GEO_KEY, circle, args);

            if (results != null) {
                return results.getContent().stream()
                        .map(geoResult -> (String) geoResult.getContent().getName())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Redis error in findNearbyBusIds, falling back to all known memory positions: {}",
                    e.getMessage());
        }

        // Search in memory fallback (simulating nearby check if Redis is down)
        return memoryFallback.values().stream()
                .filter(loc -> calculateDistance(lat, lng, loc.getLatitude(), loc.getLongitude()) <= radiusMeters)
                .map(loc -> loc.getBusId().toString())
                .collect(Collectors.toList());
    }

    // Simple Haversine distance for fallback
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // Meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
