package com.bus_tracker.service;

import com.bus_tracker.config.AppConstants;
import com.bus_tracker.dto.CommuteStatusResponse;
import com.bus_tracker.dto.StopDto;
import com.bus_tracker.dto.TodayScheduleDto;
import com.bus_tracker.entity.DailySchedule;
import com.bus_tracker.entity.Stop;
import com.bus_tracker.repository.DailyScheduleRepository;
import com.bus_tracker.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommuteService {

    private final DailyScheduleRepository scheduleRepository;
    private final StopRepository stopRepository;

    public CommuteStatusResponse getCommuteStatus(double userLat, double userLng) {
        CommuteStatusResponse response = new CommuteStatusResponse();

        // 1. Calculate distance to College
        double distanceToCollege = calculateDistance(userLat, userLng, AppConstants.COLLEGE_LAT,
                AppConstants.COLLEGE_LNG);
        response.setDistanceFromCollegeMeters(distanceToCollege);

        // 2. Geofence Logic with Buffer (30m)
        double threshold = AppConstants.COLLEGE_RADIUS_METERS + AppConstants.GEOFENCE_BUFFER_METERS;

        if (distanceToCollege <= threshold) {
            // INSIDE COLLEGE -> OUTGOING
            response.setDirection("OUTGOING");
            response.setShowDestinationSelector(true);
            response.setAssumedReason("INSIDE_COLLEGE_GEOFENCE");
            // No buses returned by default, user must select destination from frontend
            response.setAvailableBuses(List.of());
            response.setNearestStop(null);
        } else {
            // OUTSIDE COLLEGE -> INCOMING
            response.setDirection("INCOMING");
            response.setShowDestinationSelector(false); // Auto-assume "To College"
            response.setAssumedReason("OUTSIDE_COLLEGE_GEOFENCE");

            // 3. Find Nearest Stop (Simple Logic: Nearest within 2km? Or just absolute
            // nearest)
            Stop nearestStop = findNearestStop(userLat, userLng);

            if (nearestStop != null) {
                StopDto stopDto = new StopDto();
                stopDto.setId(nearestStop.getId());
                stopDto.setName(nearestStop.getName());
                stopDto.setLatitude(nearestStop.getLatitude());
                stopDto.setLongitude(nearestStop.getLongitude());
                double dist = calculateDistance(userLat, userLng, nearestStop.getLatitude(),
                        nearestStop.getLongitude());
                stopDto.setDistance(dist);
                response.setNearestStop(stopDto);
            }

            // 4. Fetch INCOMING buses (For now, fetch ALL Incoming buses.
            // Real logic would filter by route, but "Sit in any bus coming to college" rule
            // applies).
            List<DailySchedule> incomingSchedules = scheduleRepository.findByDateAndDirection(LocalDate.now(),
                    "INBOUND"); // "INBOUND" matches DataSeeder

            List<TodayScheduleDto> busDtos = incomingSchedules.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            response.setAvailableBuses(busDtos);
        }

        return response;
    }

    private Stop findNearestStop(double lat, double lng) {
        List<Stop> allStops = stopRepository.findAll();
        return allStops.stream()
                .min(Comparator.comparingDouble(s -> calculateDistance(lat, lng, s.getLatitude(), s.getLongitude())))
                .orElse(null);
    }

    private TodayScheduleDto mapToDto(DailySchedule schedule) {
        TodayScheduleDto dto = new TodayScheduleDto();
        dto.setId(schedule.getId());
        if (schedule.getBus() != null)
            dto.setBusNumber(schedule.getBus().getBusNumber());
        if (schedule.getRoute() != null)
            dto.setRouteName(schedule.getRoute().getName());
        dto.setDirection(schedule.getDirection());
        return dto;
    }

    // Haversine Formula (Meters)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }
}
