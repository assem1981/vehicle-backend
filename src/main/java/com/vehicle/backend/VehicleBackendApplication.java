package com.vehicle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJms
@EnableCaching
@EnableAsync
public class VehicleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleBackendApplication.class, args);
    }
}
