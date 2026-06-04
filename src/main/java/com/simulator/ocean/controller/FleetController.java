package com.simulator.ocean.controller;

import com.simulator.ocean.model.FleetConfig;
import com.simulator.ocean.service.BuoySimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fleet")
public class FleetController {

    private final BuoySimulationService buoySimulationService;

    public FleetController(BuoySimulationService buoySimulationService) {
        this.buoySimulationService = buoySimulationService;
    }

    @GetMapping("/config")
    public FleetConfig getFleetConfig() {
        return new FleetConfig(buoySimulationService.getCurrentBuoyCount());
    }

    @PutMapping("/config")
    public ResponseEntity<FleetConfig> updateFleetConfig(@RequestBody FleetConfig config) {
        int requestedCount = config.getBuoyCount();
        if (requestedCount < 1 || requestedCount > 200) {
            return ResponseEntity.badRequest().build();
        }
        buoySimulationService.resize(requestedCount);
        return ResponseEntity.ok(new FleetConfig(buoySimulationService.getCurrentBuoyCount()));
    }
}
