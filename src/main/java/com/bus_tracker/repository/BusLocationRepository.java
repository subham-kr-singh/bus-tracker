package com.bus_tracker.repository;

import com.bus_tracker.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
}
