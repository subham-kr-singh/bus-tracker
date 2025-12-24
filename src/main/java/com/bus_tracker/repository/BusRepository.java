package com.bus_tracker.repository;

import com.bus_tracker.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
    java.util.Optional<Bus> findByBusNumber(String busNumber);
}
