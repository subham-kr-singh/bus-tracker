package com.bus_tracker.repository;

import com.bus_tracker.entity.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {
    List<DailySchedule> findByDateAndDirection(LocalDate date, String direction);
}
