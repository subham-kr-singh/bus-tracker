package com.bus_tracker.repository;

import com.bus_tracker.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query(value = "SELECT * FROM stops ORDER BY (latitude - ?1)*(latitude - ?1) + (longitude - ?2)*(longitude - ?2) LIMIT 10", nativeQuery = true)
    List<Stop> findNearby(Double lat, Double lng);
}
