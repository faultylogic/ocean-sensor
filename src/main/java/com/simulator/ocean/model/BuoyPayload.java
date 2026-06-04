package com.simulator.ocean.model;

import com.simulator.ocean.model.sensor.AtmosphericData;
import com.simulator.ocean.model.sensor.CurrentData;
import com.simulator.ocean.model.sensor.OceanographicData;
import com.simulator.ocean.model.sensor.TidalData;
import com.simulator.ocean.model.sensor.WaveData;

import java.time.Instant;

public class BuoyPayload {

    private String buoyId;
    private String name;
    private double latitude;
    private double longitude;
    private Instant timestamp;
    private double deploymentDepthMeters;
    private BuoyStatus status;
    private AtmosphericData atmospheric;
    private OceanographicData oceanographic;
    private TidalData tidal;
    private WaveData wave;
    private CurrentData current;

    public BuoyPayload() {}

    public BuoyPayload(String buoyId, String name, double latitude, double longitude,
                       double deploymentDepthMeters, BuoyStatus status) {
        this.buoyId = buoyId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deploymentDepthMeters = deploymentDepthMeters;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public String getBuoyId() { return buoyId; }
    public void setBuoyId(String buoyId) { this.buoyId = buoyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public double getDeploymentDepthMeters() { return deploymentDepthMeters; }
    public void setDeploymentDepthMeters(double deploymentDepthMeters) { this.deploymentDepthMeters = deploymentDepthMeters; }

    public BuoyStatus getStatus() { return status; }
    public void setStatus(BuoyStatus status) { this.status = status; }

    public AtmosphericData getAtmospheric() { return atmospheric; }
    public void setAtmospheric(AtmosphericData atmospheric) { this.atmospheric = atmospheric; }

    public OceanographicData getOceanographic() { return oceanographic; }
    public void setOceanographic(OceanographicData oceanographic) { this.oceanographic = oceanographic; }

    public TidalData getTidal() { return tidal; }
    public void setTidal(TidalData tidal) { this.tidal = tidal; }

    public WaveData getWave() { return wave; }
    public void setWave(WaveData wave) { this.wave = wave; }

    public CurrentData getCurrent() { return current; }
    public void setCurrent(CurrentData current) { this.current = current; }

    @Override
    public String toString() {
        return "{" +
                "\"buoyId\":\"" + buoyId + "\"" +
                ",\"name\":\"" + name + "\"" +
                ",\"latitude\":" + latitude +
                ",\"longitude\":" + longitude +
                ",\"timestamp\":\"" + (timestamp != null ? timestamp.toString() : "") + "\"" +
                ",\"deploymentDepthMeters\":" + deploymentDepthMeters +
                ",\"status\":\"" + (status != null ? status.name() : "") + "\"" +
                ",\"atmospheric\":" + (atmospheric != null ? atmospheric.toString() : "null") +
                ",\"oceanographic\":" + (oceanographic != null ? oceanographic.toString() : "null") +
                ",\"tidal\":" + (tidal != null ? tidal.toString() : "null") +
                ",\"wave\":" + (wave != null ? wave.toString() : "null") +
                ",\"current\":" + (current != null ? current.toString() : "null") +
                "}";
    }
}
