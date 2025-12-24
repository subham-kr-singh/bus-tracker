package com.bus_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = "com.bus_tracker.repository")
public class BusTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BusTrackerApplication.class, args);
	}
}
