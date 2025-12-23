package com.bus_tracker.service;

import com.bus_tracker.entity.BusLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, BusLocation> redisTemplate;

    private static final String LOCATION_KEY = "bus:location:%d";

    public void updateLocation(Long busId, Double lat, Double lng, Double speed) {
        BusLocation location = new BusLocation();
        location.setBusId(busId);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setSpeed(speed);
        location.setLastUpdatedAt(java.time.LocalDateTime.now());

        redisTemplate.opsForValue()
                .set(String.format(LOCATION_KEY, busId), location, 5, TimeUnit.MINUTES);
    }

    public BusLocation getLatestLocation(Long busId) {
        return (BusLocation) redisTemplate.opsForValue()
                .get(String.format(LOCATION_KEY, busId));
    }
}
