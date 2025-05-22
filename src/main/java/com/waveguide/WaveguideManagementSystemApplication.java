package com.waveguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WaveguideManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaveguideManagementSystemApplication.class, args);
    }
}