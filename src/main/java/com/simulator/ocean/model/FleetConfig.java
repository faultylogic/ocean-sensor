package com.simulator.ocean.model;

public class FleetConfig {

    /** Number of active buoys in the simulated fleet (1-200) */
    private int buoyCount;

    public FleetConfig() {}

    public FleetConfig(int buoyCount) {
        this.buoyCount = buoyCount;
    }

    public int getBuoyCount() { return buoyCount; }
    public void setBuoyCount(int buoyCount) { this.buoyCount = buoyCount; }
}
