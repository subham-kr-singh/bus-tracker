package com.bus_tracker.controller;

import com.bus_tracker.dto.UpdateScheduleDto;
import com.bus_tracker.entity.Bus;
import com.bus_tracker.service.BusService;
import com.bus_tracker.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BusService busService;
    private final ScheduleService scheduleService;
    private final com.bus_tracker.repository.RouteRepository routeRepository;

    @GetMapping("/buses")
    public List<Bus> getBuses() {
        return busService.getAllBuses();
    }

    @PostMapping("/buses")
    public Bus createBus(@RequestBody Bus bus) {
        return busService.createBus(bus);
    }

    @GetMapping("/routes")
    public List<com.bus_tracker.entity.Route> getRoutes() {
        return routeRepository.findAll();
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody com.bus_tracker.dto.CreateScheduleDto dto) {
        scheduleService.createSchedule(dto.getRouteId(), dto.getBusId(), dto.getDirection());
        return ResponseEntity.ok("Schedule Created");
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody UpdateScheduleDto dto) {
        scheduleService.updateBusAssignment(id, dto.getBusId());
        return ResponseEntity.ok().build();
    }
}
