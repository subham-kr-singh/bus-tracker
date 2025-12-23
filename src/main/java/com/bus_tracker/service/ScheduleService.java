package com.bus_tracker.service;

import com.bus_tracker.dto.MorningBusDto;
import com.bus_tracker.repository.DailyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DailyScheduleRepository scheduleRepository;

    public List<MorningBusDto> getMorningBuses(LocalDate date, Long stopId) {
        // TODO: Real logic here
        return List.of();
    }
}
