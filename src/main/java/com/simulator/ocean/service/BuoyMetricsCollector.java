package com.simulator.ocean.service;

import com.simulator.ocean.model.BuoyPayload;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BuoyMetricsCollector {

    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicReference<BuoyPayload>> snapshots = new ConcurrentHashMap<>();

    public BuoyMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(BuoyPayload buoy) {
        snapshots.computeIfAbsent(buoy.getBuoyId(), id -> {
            AtomicReference<BuoyPayload> ref = new AtomicReference<>(buoy);

            // Identity metric — always 1.0, useful for Dynatrace variable binding
            Gauge.builder("buoy.info", ref, r -> 1.0)
                    .tag("buoy_id", id).register(meterRegistry);

            // Position
            Gauge.builder("buoy.latitude", ref, r -> r.get().getLatitude())
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.longitude", ref, r -> r.get().getLongitude())
                    .tag("buoy_id", id).register(meterRegistry);

            // Atmospheric gauges
            Gauge.builder("buoy.atm.co2.ppm", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getCo2Ppm() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.pressure.hpa", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getBarometricPressureHpa() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.air.temp.celsius", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getAirTempCelsius() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.wind.speed.knots", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getWindSpeedKnots() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.wind.direction.degrees", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getWindDirectionDegrees() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.humidity.percent", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getRelativeHumidityPercent() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.atm.solar.irradiance.wm2", ref,
                            r -> r.get().getAtmospheric() != null ? r.get().getAtmospheric().getSolarIrradianceWm2() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);

            // Oceanographic gauges
            Gauge.builder("buoy.ocean.surface.temp.celsius", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getSurfaceTempCelsius() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.subsurface.temp.celsius", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getSubsurfaceTempCelsius() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.salinity.psu", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getSalinityPsu() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.ph", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getPhLevel() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.dissolved.oxygen.mgl", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getDissolvedOxygenMgL() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.turbidity.ntu", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getTurbidityNtu() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.chlorophyll.ugl", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getChlorophyllUgL() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.ocean.dissolved.co2.umol", ref,
                            r -> r.get().getOceanographic() != null ? r.get().getOceanographic().getDissolvedCo2Umol() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);

            // Tidal gauges
            Gauge.builder("buoy.tidal.water.level.meters", ref,
                            r -> r.get().getTidal() != null ? r.get().getTidal().getWaterLevelMeters() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.tidal.rate.cm.per.hour", ref,
                            r -> r.get().getTidal() != null ? r.get().getTidal().getTidalRateCmPerHour() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.tidal.range.meters", ref,
                            r -> r.get().getTidal() != null ? r.get().getTidal().getTidalRangeMeters() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.tidal.next.change.minutes", ref,
                            r -> r.get().getTidal() != null ? (double) r.get().getTidal().getNextTideChangeMinutes() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);

            // Wave gauges
            Gauge.builder("buoy.wave.significant.height.meters", ref,
                            r -> r.get().getWave() != null ? r.get().getWave().getSignificantWaveHeightMeters() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.wave.max.height.meters", ref,
                            r -> r.get().getWave() != null ? r.get().getWave().getMaxWaveHeightMeters() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.wave.peak.period.seconds", ref,
                            r -> r.get().getWave() != null ? r.get().getWave().getPeakWavePeriodSeconds() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.wave.direction.degrees", ref,
                            r -> r.get().getWave() != null ? r.get().getWave().getWaveDirectionDegrees() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.wave.swell.height.meters", ref,
                            r -> r.get().getWave() != null ? r.get().getWave().getSwellHeightMeters() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);

            // Current gauges
            Gauge.builder("buoy.current.surface.speed.knots", ref,
                            r -> r.get().getCurrent() != null ? r.get().getCurrent().getSurfaceCurrentSpeedKnots() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.current.surface.direction.degrees", ref,
                            r -> r.get().getCurrent() != null ? r.get().getCurrent().getSurfaceCurrentDirectionDegrees() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.current.subsurface.speed.knots", ref,
                            r -> r.get().getCurrent() != null ? r.get().getCurrent().getSubsurfaceCurrentSpeedKnots() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);
            Gauge.builder("buoy.current.vertical.cm.per.sec", ref,
                            r -> r.get().getCurrent() != null ? r.get().getCurrent().getVerticalCurrentCmPerSec() : 0.0)
                    .tag("buoy_id", id).register(meterRegistry);

            return ref;
        }).set(buoy);

        meterRegistry.counter("buoy.updates.total", "buoy_id", buoy.getBuoyId()).increment();
    }
}
