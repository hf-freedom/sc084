package com.park.energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnergyManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyManagementApplication.class, args);
    }
}
