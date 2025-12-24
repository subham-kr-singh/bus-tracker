package com.bus_tracker.service;

import com.bus_tracker.dto.StopDto;
import com.bus_tracker.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StopService {

    private final StopRepository stopRepository;

    public List<StopDto> findNearby(Double lat, Double lng, Double radius) {
        // Radius is ignored in the simple native query implementation for now
        return stopRepository.findNearby(lat, lng).stream()
                .map(stop -> {
                    StopDto dto = new StopDto();
                    dto.setId(stop.getId());
                    dto.setName(stop.getName());
                    dto.setLatitude(stop.getLatitude());
                    dto.setLongitude(stop.getLongitude());
                    dto.setDistance(calculateDistance(lat, lng, stop.getLatitude(), stop.getLongitude()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // Simple Euclidean distance for prototype
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
    }
}
