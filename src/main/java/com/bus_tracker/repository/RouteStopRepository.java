package com.bus_tracker.repository;

import com.bus_tracker.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteIdOrderBySequenceNumberAsc(Long routeId);

    Optional<RouteStop> findByRouteIdAndStopId(Long routeId, Long stopId);
}
