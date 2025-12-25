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
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder,
            com.bus_tracker.repository.RouteRepository routeRepository,
            com.bus_tracker.repository.BusRepository busRepository,
            com.bus_tracker.repository.StopRepository stopRepository) {
        return args -> {
            try {
                log.info("Starting Data Seeding...");

                createOrUpdateUser(userRepository, passwordEncoder, "admin@example.com", "password123", "System Admin",
                        "ADMIN", "0000000000");
                createOrUpdateUser(userRepository, passwordEncoder, "driver@example.com", "password123", "Test Driver",
                        "DRIVER", "1111111111");
                createOrUpdateUser(userRepository, passwordEncoder, "student@example.com", "password123",
                        "Test Student", "STUDENT", "2222222222");

                seedRoutes(routeRepository);
                seedBuses(busRepository);
                seedStops(stopRepository);

                log.info("Data Seeding Completed.");
            } catch (Exception e) {
                log.error("Data seeding failed, but application will continue: {}", e.getMessage());
                log.error("Full error:", e);
            }
        };
    }

    private void createOrUpdateUser(UserRepository repo, PasswordEncoder encoder, String email, String password,
            String name, String role, String phone) {
        User user = repo.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setPhone(phone);
        repo.save(user);
        log.info("User {} synced with password: {}", email, password);
    }

    private void seedRoutes(com.bus_tracker.repository.RouteRepository repo) {
        if (repo.count() == 0) {
            log.info("Seeding Routes...");
            repo.save(createRoute("Oriental to Indrapuri", "OUTBOUND"));
            repo.save(createRoute("Indrapuri to Oriental", "INBOUND"));
            repo.save(createRoute("Oriental to Anand Nagar", "OUTBOUND"));
            repo.save(createRoute("Anand Nagar to Oriental", "INBOUND"));
            repo.save(createRoute("Oriental to MP Nagar", "OUTBOUND"));
            repo.save(createRoute("MP Nagar to Oriental", "INBOUND"));
        }
    }

    private com.bus_tracker.entity.Route createRoute(String name, String direction) {
        com.bus_tracker.entity.Route r = new com.bus_tracker.entity.Route();
        r.setName(name);
        r.setDirection(direction);
        return r;
    }

    private void seedBuses(com.bus_tracker.repository.BusRepository repo) {
        if (repo.count() == 0) {
            log.info("Seeding Buses...");
            repo.save(createBus("ORI-01", 40));
            repo.save(createBus("ORI-02", 40));
            repo.save(createBus("ORI-03", 50));
            repo.save(createBus("ORI-04", 50));
            repo.save(createBus("ORI-05", 30));
        }
    }

    private com.bus_tracker.entity.Bus createBus(String number, int capacity) {
        com.bus_tracker.entity.Bus b = new com.bus_tracker.entity.Bus();
        b.setBusNumber(number);
        b.setCapacity(capacity);
        b.setStatus("IDLE");
        return b;
    }

    private void seedStops(com.bus_tracker.repository.StopRepository repo) {
        if (repo.count() == 0) {
            log.info("Seeding Stops...");
            // Oriental College (Approx Location)
            repo.save(createStop("Oriental College Main Gate", 23.259933, 77.412615));
            // Indrapuri (Approx)
            repo.save(createStop("Indrapuri C Sector", 23.2167, 77.4500));
            // Anand Nagar
            repo.save(createStop("Anand Nagar Market", 23.2300, 77.4600));
            // MP Nagar
            repo.save(createStop("MP Nagar Zone 1", 23.2324, 77.4303));
            // Chetak Bridge
            repo.save(createStop("Chetak Bridge", 23.2350, 77.4450));
        }
    }

    private com.bus_tracker.entity.Stop createStop(String name, double lat, double lng) {
        com.bus_tracker.entity.Stop s = new com.bus_tracker.entity.Stop();
        s.setName(name);
        s.setLatitude(lat);
        s.setLongitude(lng);
        return s;
    }

}
