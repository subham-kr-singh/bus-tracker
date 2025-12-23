package com.bus_tracker.controller;

import com.bus_tracker.dto.LocationUpdateDto;
import com.bus_tracker.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    private final TrackingService trackingService;

    @PostMapping("/location")
    public void updateLocation(@RequestBody LocationUpdateDto locationUpdate) {
        // TODO: Implement location update logic
    }
}
