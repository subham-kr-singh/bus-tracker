package com.bus_tracker.controller;

import com.bus_tracker.entity.Bus;
import com.bus_tracker.entity.DailySchedule;
import com.bus_tracker.service.BusService;
import com.bus_tracker.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BusService busService;
    private final ScheduleService scheduleService;

    @PostMapping("/buses")
    public Bus createBus(@RequestBody Bus bus) {
        // TODO: Implement bus creation logic
        return bus;
    }

    @PutMapping("/schedules/{id}")
    public DailySchedule updateSchedule(@PathVariable Long id, @RequestBody DailySchedule schedule) {
        // TODO: Implement schedule update logic
        return schedule;
    }
}
