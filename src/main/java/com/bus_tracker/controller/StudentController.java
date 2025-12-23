package com.bus_tracker.controller;

import com.bus_tracker.dto.MorningBusDto;
import com.bus_tracker.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final ScheduleService scheduleService;

    @GetMapping("/morning-buses")
    public List<MorningBusDto> getMorningBuses(
            @RequestParam LocalDate date,
            @RequestParam Long stopId) {
        return scheduleService.getMorningBuses(date, stopId);
    }

    @GetMapping("/stops/nearby")
    public List<Object> getNearbyStops(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Double radius) {
        // TODO: Implement spatial search logic
        return List.of();
    }
}
