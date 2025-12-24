package com.bus_tracker.repository;

import com.bus_tracker.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {
    /**
     * Finds stops within a certain radius using the Haversine formula.
     * 6371 is Earth's radius in km.
     * 
     * Formula:
     * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     * c = 2 ⋅ atan2( √a, √(1−a) )
     * d = R ⋅ c
     * 
     * Simplified SQL version for "nearest 10":
     */
    @Query(value = """
        SELECT * FROM stops 
        ORDER BY (
            6371 * acos(
                cos(radians(?1)) * cos(radians(latitude)) * 
                cos(radians(longitude) - radians(?2)) + 
                sin(radians(?1)) * sin(radians(latitude))
            )
        ) ASC 
        LIMIT 10
        """, nativeQuery = true)
    // @Cacheable("nearbyStops") // TODO: Enable cache manager
    List<Stop> findNearby(Double lat, Double lng);
}
