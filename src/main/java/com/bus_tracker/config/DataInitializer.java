package com.bus_tracker.config;

import com.bus_tracker.entity.Bus;
import com.bus_tracker.entity.User;
import com.bus_tracker.repository.BusRepository;
import com.bus_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Add Admin
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@college.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);

            // Add Driver
            User driver = new User();
            driver.setName("Driver John");
            driver.setEmail("driver@college.com");
            driver.setPasswordHash(passwordEncoder.encode("driver123"));
            driver.setRole("DRIVER");
            userRepository.save(driver);

            // Add Student
            User student = new User();
            student.setName("Student Alice");
            student.setEmail("student@college.com");
            student.setPasswordHash(passwordEncoder.encode("student123"));
            student.setRole("STUDENT");
            userRepository.save(student);

            System.out.println("Test users initialized: admin@college.com, driver@college.com, student@college.com");
        }

        if (busRepository.count() == 0) {
            Bus bus1 = new Bus();
            bus1.setBusNumber("KA-01-1234");
            bus1.setCapacity(50);
            bus1.setGpsDeviceId("GPS-001");
            busRepository.save(bus1);

            Bus bus2 = new Bus();
            bus2.setBusNumber("KA-01-5678");
            bus2.setCapacity(40);
            bus2.setGpsDeviceId("GPS-002");
            busRepository.save(bus2);

            System.out.println("Test buses initialized.");
        }
    }
}
