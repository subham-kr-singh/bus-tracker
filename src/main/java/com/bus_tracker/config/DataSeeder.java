package com.bus_tracker.config;

import com.bus_tracker.entity.User;
import com.bus_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("Starting Data Seeding...");

            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                log.info("Creating default ADMIN user...");
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(passwordEncoder.encode("password123"));
                admin.setName("System Admin");
                admin.setRole("ADMIN");
                admin.setPhone("0000000000");
                userRepository.save(admin);
                log.info("ADMIN user created: admin@example.com / password123");
            } else {
                log.info("ADMIN user already exists.");
            }

            if (userRepository.findByEmail("driver@example.com").isEmpty()) {
                log.info("Creating default DRIVER user...");
                User driver = new User();
                driver.setEmail("driver@example.com");
                driver.setPasswordHash(passwordEncoder.encode("password123"));
                driver.setName("Test Driver");
                driver.setRole("DRIVER");
                driver.setPhone("1111111111");
                userRepository.save(driver);
                log.info("DRIVER user created: driver@example.com / password123");
            }

            if (userRepository.findByEmail("student@example.com").isEmpty()) {
                log.info("Creating default STUDENT user...");
                User student = new User();
                student.setEmail("student@example.com");
                student.setPasswordHash(passwordEncoder.encode("password123"));
                student.setName("Test Student");
                student.setRole("STUDENT");
                student.setPhone("2222222222");
                userRepository.save(student);
                log.info("STUDENT user created: student@example.com / password123");
            }

            log.info("Data Seeding Completed.");
        };
    }
}
