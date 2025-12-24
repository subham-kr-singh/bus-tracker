package com.bus_tracker.controller;

import com.bus_tracker.dto.MorningBusDto;
import com.bus_tracker.dto.StopDto;
import com.bus_tracker.service.ScheduleService;
import com.bus_tracker.service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final StopService stopService;

    @GetMapping("/morning-buses")
    public List<MorningBusDto> getMorningBuses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long stopId) {
        return scheduleService.getMorningBuses(date, stopId);
    }

    @GetMapping("/stops/nearby")
    public List<StopDto> getNearbyStops(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1000") Double radius) {
        return stopService.findNearby(lat, lng, radius);
    }
}
