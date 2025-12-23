package com.bus_tracker.service;

import com.bus_tracker.dto.MorningBusDto;
import com.bus_tracker.entity.*;
import com.bus_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DailyScheduleRepository scheduleRepository;
    private final RouteStopRepository routeStopRepository;
    private final BusRepository busRepository;
    private final LocationService locationService;

    public List<MorningBusDto> getMorningBuses(LocalDate date, Long stopId) {
        List<DailySchedule> schedules = scheduleRepository
                .findByDateAndDirection(date, "MORNING");

        return schedules.stream()
                .filter(schedule -> routeServesStop(schedule.getRoute().getId(), stopId))
                .map(this::toMorningBusDto)
                .collect(Collectors.toList());
    }

    private boolean routeServesStop(Long routeId, Long stopId) {
        return routeStopRepository.findByRouteIdAndStopId(routeId, stopId).isPresent();
    }

    private MorningBusDto toMorningBusDto(DailySchedule schedule) {
        Bus bus = schedule.getBus();
        BusLocation location = locationService.getLatestLocation(bus.getId());

        MorningBusDto dto = new MorningBusDto();
        dto.setBusId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setRouteName(schedule.getRoute().getName());
        dto.setEtaMinutes(calculateEta(location, schedule)); // TODO: Implement
        dto.setLatitude(location != null ? location.getLatitude() : 0.0);
        dto.setLongitude(location != null ? location.getLongitude() : 0.0);
        return dto;
    }

    private int calculateEta(BusLocation location, DailySchedule schedule) {
        // Simple distance/speed calculation (implement haversine later)
        return 10; // Placeholder
    }
}
