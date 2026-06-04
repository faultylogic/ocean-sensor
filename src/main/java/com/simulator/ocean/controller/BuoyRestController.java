package com.simulator.ocean.controller;

import com.simulator.ocean.model.BuoyPayload;
import com.simulator.ocean.service.BuoySimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/buoys")
public class BuoyRestController {

    private final BuoySimulationService buoySimulationService;

    public BuoyRestController(BuoySimulationService buoySimulationService) {
        this.buoySimulationService = buoySimulationService;
    }

    @GetMapping
    public Collection<BuoyPayload> getAllBuoys() {
        return buoySimulationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuoyPayload> getBuoyById(@PathVariable String id) {
        BuoyPayload buoy = buoySimulationService.getById(id);
        if (buoy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(buoy);
    }
}
