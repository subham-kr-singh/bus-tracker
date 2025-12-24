package com.bus_tracker.service;

import com.bus_tracker.entity.BusLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCATION_GAME_KEY = "bus:location:%d";
    private static final String GEO_KEY = "buses:geo";

    public void updateLocation(Long busId, Double lat, Double lng, Double speed) {
        if (busId == null || lat == null || lng == null) {
            throw new IllegalArgumentException("busId, latitude, and longitude cannot be null");
        }

        BusLocation location = new BusLocation();
        location.setBusId(busId);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setSpeed(speed);
        location.setLastUpdatedAt(java.time.LocalDateTime.now());

        // 1. Store detailed info (Value operation)
        String key = String.format(LOCATION_GAME_KEY, busId);
        redisTemplate.opsForValue().set(key, location, 5, TimeUnit.MINUTES);

        // 2. Store Geospatial data (Geo operation)
        String busIdStr = busId.toString();
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(lng, lat), busIdStr);
    }

    public BusLocation getLatestLocation(Long busId) {
        if (busId == null) {
            return null;
        }
        // Retrieve explicit details
        Object result = redisTemplate.opsForValue().get(String.format(LOCATION_GAME_KEY, busId));
        return result instanceof BusLocation ? (BusLocation) result : null;
    }

    /**
     * Finds bus IDs within a certain radius (in meters) of a point.
     * This is an O(log(N)) operation handled entirely by Redis.
     */
    public List<String> findNearbyBusIds(Double lat, Double lng, double radiusMeters) {
        Circle circle = new Circle(new Point(lng, lat), new Distance(radiusMeters, DistanceUnit.METERS));
        // Simple radius command args
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance();

        var results = redisTemplate.opsForGeo().radius(GEO_KEY, circle, args);

        if (results == null)
            return new ArrayList<>();

        return results.getContent().stream()
                .map(geoResult -> (String) geoResult.getContent().getName())
                .collect(Collectors.toList());
    }
}
