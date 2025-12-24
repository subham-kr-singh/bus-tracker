package com.bus_tracker.service;

import com.bus_tracker.dto.LocationUpdateDto;
import com.bus_tracker.dto.TodayScheduleDto;
import com.bus_tracker.entity.DailySchedule;
import com.bus_tracker.repository.DailyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final DailyScheduleRepository scheduleRepository;
    private final LocationService locationService;

    public List<TodayScheduleDto> getTodaySchedules(LocalDate date) {
        return scheduleRepository.findByDateAndDirection(date, "MORNING").stream()
                .map(this::toTodayScheduleDto)
                .collect(Collectors.toList());
    }

    public void updateLocation(LocationUpdateDto dto) {
        DailySchedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        locationService.updateLocation(
                schedule.getBus().getId(),
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getSpeed());
    }

    private TodayScheduleDto toTodayScheduleDto(DailySchedule schedule) {
        TodayScheduleDto dto = new TodayScheduleDto();
        dto.setId(schedule.getId());
        dto.setBusNumber(schedule.getBus().getBusNumber());
        dto.setRouteName(schedule.getRoute().getName());
        dto.setDirection(schedule.getDirection());
        return dto;
    }
}
