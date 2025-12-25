package com.bus_tracker.service;

import com.bus_tracker.dto.LocationUpdateDto;
import com.bus_tracker.dto.TodayScheduleDto;
import com.bus_tracker.entity.Bus;
import com.bus_tracker.entity.BusLocation;
import com.bus_tracker.entity.DailySchedule;
import com.bus_tracker.repository.BusRepository;
import com.bus_tracker.repository.DailyScheduleRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final DailyScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final LocationService locationService;
    private final SimpMessagingTemplate messagingTemplate;

    public List<TodayScheduleDto> getTodaySchedules(LocalDate date) {
        return scheduleRepository.findByDate(date).stream()
                .map(this::toTodayScheduleDto)
                .collect(Collectors.toList());
    }

    public void updateLocation(LocationUpdateDto dto) {
        Long busId;

        if (dto.getScheduleId() != null) {
            DailySchedule schedule = scheduleRepository.findById(dto.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
            if (schedule.getBus() == null)
                throw new RuntimeException("Schedule has no bus assigned");
            busId = schedule.getBus().getId();
        } else if (dto.getBusNumber() != null) {
            Bus bus = busRepository.findByBusNumber(dto.getBusNumber())
                    .orElseThrow(() -> new RuntimeException("Bus with number " + dto.getBusNumber() + " not found"));
            busId = bus.getId();
        } else {
            throw new IllegalArgumentException("Either scheduleId or busNumber must be provided");
        }

        if (busId == null)
            throw new RuntimeException("Bus ID is null");

        BusLocation updatedLocation = locationService.updateLocation(
                busId,
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getSpeed());

        // Broadcast to subscribers
        messagingTemplate.convertAndSend("/topic/bus/" + busId, updatedLocation);
    }

    private TodayScheduleDto toTodayScheduleDto(DailySchedule schedule) {
        TodayScheduleDto dto = new TodayScheduleDto();
        dto.setId(schedule.getId());
        if (schedule.getBus() != null) {
            dto.setBusNumber(schedule.getBus().getBusNumber());
        }
        if (schedule.getRoute() != null) {
            dto.setRouteName(schedule.getRoute().getName());
        }
        dto.setDirection(schedule.getDirection());
        return dto;
    }

    public List<BusLocation> getAllLiveLocations() {
        return scheduleRepository.findByDate(LocalDate.now()).stream()
                .map(schedule -> locationService.getLatestLocation(schedule.getBus().getId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
