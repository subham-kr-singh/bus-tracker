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
            try {
                log.info("Starting Data Seeding...");

                createOrUpdateUser(userRepository, passwordEncoder, "admin@example.com", "password123", "System Admin", "ADMIN", "0000000000");
                createOrUpdateUser(userRepository, passwordEncoder, "driver@example.com", "password123", "Test Driver", "DRIVER", "1111111111");
                createOrUpdateUser(userRepository, passwordEncoder, "student@example.com", "password123", "Test Student", "STUDENT", "2222222222");

                log.info("Data Seeding Completed.");
            } catch (Exception e) {
                log.error("Data seeding failed, but application will continue: {}", e.getMessage());
                log.error("Full error:", e);
            }
        };
    }

    private void createOrUpdateUser(UserRepository repo, PasswordEncoder encoder, String email, String password, String name, String role, String phone) {
        User user = repo.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setPhone(phone);
        repo.save(user);
        log.info("User {} synced with password: {}", email, password);
    }

                log.info("Data Seeding Completed.");
            } catch (Exception e) {
                log.error("Data seeding failed, but application will continue: {}", e.getMessage());
                log.error("Full error:", e);
            }
        };}}
