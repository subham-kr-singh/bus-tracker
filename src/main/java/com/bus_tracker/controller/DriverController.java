package com.bus_tracker.controller;

import com.bus_tracker.dto.LocationUpdateDto;
import com.bus_tracker.dto.TodayScheduleDto;
import com.bus_tracker.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    private final TrackingService trackingService;

    @GetMapping("/schedules/today")
    public List<TodayScheduleDto> getTodaySchedules() {
        return trackingService.getTodaySchedules(LocalDate.now());
    }

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateDto dto) {
        trackingService.updateLocation(dto);
        return ResponseEntity.ok("Location updated");
    }
}
