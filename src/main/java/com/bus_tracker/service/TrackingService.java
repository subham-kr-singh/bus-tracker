package com.bus_tracker.service;

import com.bus_tracker.dto.LocationUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOCATION_KEY_PREFIX = "bus:location:";

    public void updateBusLocation(LocationUpdateDto update) {
        String key = LOCATION_KEY_PREFIX + update.getScheduleId();
        redisTemplate.opsForValue().set(key, update);
        // Expire after 5 minutes if no updates received
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    public LocationUpdateDto getBusLocation(Long scheduleId) {
        String key = LOCATION_KEY_PREFIX + scheduleId;
        return (LocationUpdateDto) redisTemplate.opsForValue().get(key);
    }
}
