package com.simulator.ocean;

import com.simulator.ocean.config.BindPlaneProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(BindPlaneProperties.class)
public class OceanSensorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OceanSensorApplication.class, args);
    }
}
