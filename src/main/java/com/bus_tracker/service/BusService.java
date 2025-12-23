package com.bus_tracker.service;

import com.bus_tracker.entity.Bus;
import com.bus_tracker.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus createBus(Bus bus) {
        return busRepository.save(bus);
    }
}
