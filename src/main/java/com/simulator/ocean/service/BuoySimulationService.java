package com.simulator.ocean.service;

import com.simulator.ocean.model.BuoyPayload;
import com.simulator.ocean.model.BuoyStatus;
import com.simulator.ocean.model.sensor.AtmosphericData;
import com.simulator.ocean.model.sensor.CurrentData;
import com.simulator.ocean.model.sensor.OceanographicData;
import com.simulator.ocean.model.sensor.TidalData;
import com.simulator.ocean.model.sensor.WaveData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service("buoySimulationService")
public class BuoySimulationService {

    private static final Logger log = LoggerFactory.getLogger(BuoySimulationService.class);

    // Geographic centre — Halifax Approaches, Nova Scotia
    private static final double BASE_LAT = 44.6488;
    private static final double BASE_LON = -62.314;
    private static final double METERS_PER_DEGREE_LAT = 111_320.0;
    private static final double SPAWN_RADIUS_METERS = 80_000.0; // 80 km radius
    private static final double MAX_DRIFT_METERS = 5.0;          // buoys drift slowly (anchored)
    private static final long   TICK_MS = 5_000L;               // 5-second tick
    private static final long   TIDAL_PERIOD_MS = 44_712_000L;  // 12h 25min lunar tidal cycle

    private static final String[] BUOY_NAMES = {
            "Halifax Approaches Buoy",
            "Sable Island East Buoy",
            "Cabot Strait North Buoy",
            "Scotian Shelf Buoy",
            "Grand Banks West Buoy",
            "Labrador Current Buoy",
            "St. Lawrence South Buoy",
            "Emerald Basin Buoy"
    };

    @Value("${simulator.buoy-count:8}")
    private volatile int buoyCount;

    private final SimpMessagingTemplate messagingTemplate;
    private final BuoyMetricsCollector metricsCollector;
    private final Map<String, BuoyPayload> fleet = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public BuoySimulationService(SimpMessagingTemplate messagingTemplate,
                                  BuoyMetricsCollector metricsCollector) {
        this.messagingTemplate = messagingTemplate;
        this.metricsCollector = metricsCollector;
    }

    @PostConstruct
    public void init() {
        log.info("Initialising ocean-sensor fleet with {} buoys", buoyCount);
        for (int i = 1; i <= buoyCount; i++) {
            spawnBuoy(i);
        }
        log.info("Fleet initialised: {} buoys active", fleet.size());
    }

    private void spawnBuoy(int index) {
        String id = String.format("BUOY-%03d", index);
        String name = BUOY_NAMES[(index - 1) % BUOY_NAMES.length] + " " + index;

        // Scatter buoys randomly within SPAWN_RADIUS from base coordinates
        double angle = random.nextDouble() * 2 * Math.PI;
        double dist  = random.nextDouble() * SPAWN_RADIUS_METERS;
        double lat = BASE_LAT + (dist * Math.sin(angle)) / METERS_PER_DEGREE_LAT;
        double metersPerDegreeLon = METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(BASE_LAT));
        double lon = BASE_LON + (dist * Math.cos(angle)) / metersPerDegreeLon;

        double deploymentDepth = 30.0 + random.nextDouble() * 470.0; // 30-500 m

        // ── Atmospheric initial values ────────────────────────────────────────────
        double co2Ppm            = 415.0 + random.nextDouble() * 10.0;
        double pressureHpa       = 1008.0 + random.nextDouble() * 15.0;
        double airTempC          = 8.0 + random.nextDouble() * 12.0;
        double windSpeedKn       = 5.0 + random.nextDouble() * 20.0;
        double windDirDeg        = random.nextDouble() * 360.0;
        double humidityPct       = 65.0 + random.nextDouble() * 30.0;
        double solarWm2          = random.nextDouble() * 800.0;
        AtmosphericData atm = new AtmosphericData(
                co2Ppm, pressureHpa, airTempC, windSpeedKn, windDirDeg, humidityPct, solarWm2);

        // ── Oceanographic initial values ─────────────────────────────────────────
        double surfaceTempC      = 6.0 + random.nextDouble() * 14.0;
        double subsurfaceTempC   = surfaceTempC - (1.0 + random.nextDouble() * 3.0);
        double salinityPsu       = 32.0 + random.nextDouble() * 3.0;
        double ph                = 8.05 + random.nextDouble() * 0.15;
        double dissolvedO2       = 7.0 + random.nextDouble() * 4.0;
        double turbidityNtu      = 0.5 + random.nextDouble() * 3.0;
        double chlorophyllUgL    = 0.2 + random.nextDouble() * 5.0;
        double dissolvedCo2Umol  = 15.0 + random.nextDouble() * 10.0;
        OceanographicData ocean = new OceanographicData(
                surfaceTempC, subsurfaceTempC, salinityPsu, ph,
                dissolvedO2, turbidityNtu, chlorophyllUgL, dissolvedCo2Umol);

        // ── Tidal initial values (sinusoidal based on current time) ──────────────
        double tidalRange        = 1.0 + random.nextDouble() * 1.5; // Halifax ~1.5 m range
        double highTide          = 0.5 + tidalRange;
        double lowTide           = 0.5;
        double midLevel          = (highTide + lowTide) / 2.0;
        long now                 = System.currentTimeMillis();
        double tidalAngle        = (2 * Math.PI * now) / TIDAL_PERIOD_MS;
        double waterLevel        = midLevel + (tidalRange / 2.0) * Math.sin(tidalAngle);
        double tidalRate         = (tidalRange / 2.0) * Math.cos(tidalAngle)
                                   * (2 * Math.PI / (TIDAL_PERIOD_MS / 1000.0)) * 3600.0;
        String tidalPhase        = computeTidalPhase(tidalAngle);
        int nextChangeMinutes    = computeNextTideChangeMinutes(tidalAngle);
        TidalData tidal = new TidalData(
                waterLevel, tidalRate, tidalPhase, highTide, lowTide,
                nextChangeMinutes, tidalRange);

        // ── Wave initial values ───────────────────────────────────────────────────
        double waveHs            = 0.3 + random.nextDouble() * 2.0;
        double waveMax           = waveHs * (1.4 + random.nextDouble() * 0.4);
        double peakPeriod        = 6.0 + random.nextDouble() * 8.0;
        double meanPeriod        = peakPeriod * 0.85;
        double waveDirDeg        = random.nextDouble() * 360.0;
        double swellHeight       = waveHs * 0.6;
        double swellPeriod       = peakPeriod + 2.0 + random.nextDouble() * 4.0;
        double swellDir          = (waveDirDeg + 20.0 + random.nextDouble() * 30.0) % 360.0;
        WaveData wave = new WaveData(
                waveHs, waveMax, peakPeriod, meanPeriod,
                waveDirDeg, swellHeight, swellPeriod, swellDir);

        // ── Current initial values ────────────────────────────────────────────────
        double surfCurrentSpeed  = 0.1 + random.nextDouble() * 0.8;
        double surfCurrentDir    = random.nextDouble() * 360.0;
        double subCurrentSpeed   = surfCurrentSpeed * (0.5 + random.nextDouble() * 0.4);
        double subCurrentDir     = (surfCurrentDir + random.nextDouble() * 30.0 - 15.0 + 360.0) % 360.0;
        double vertCurrentCmS    = (random.nextDouble() - 0.5) * 0.4; // small vertical component
        CurrentData current = new CurrentData(
                surfCurrentSpeed, surfCurrentDir,
                subCurrentSpeed, subCurrentDir, vertCurrentCmS);

        BuoyPayload buoy = new BuoyPayload(id, name, lat, lon, deploymentDepth, BuoyStatus.OPERATIONAL);
        buoy.setAtmospheric(atm);
        buoy.setOceanographic(ocean);
        buoy.setTidal(tidal);
        buoy.setWave(wave);
        buoy.setCurrent(current);

        fleet.put(id, buoy);
        metricsCollector.record(buoy);
        log.debug("Spawned {} at ({}, {})", id, lat, lon);
    }

    @Scheduled(fixedRate = TICK_MS)
    public void tick() {
        fleet.values().forEach(this::update);
        messagingTemplate.convertAndSend("/topic/buoys", fleet.values());
    }

    private void update(BuoyPayload buoy) {
        // ── Tidal simulation (sinusoidal) ─────────────────────────────────────────
        TidalData tidal = buoy.getTidal();
        if (tidal != null) {
            long now = System.currentTimeMillis();
            double tidalAngle = (2 * Math.PI * now) / TIDAL_PERIOD_MS;
            double tidalRange = tidal.getTidalRangeMeters();
            double midLevel   = (tidal.getPredictedHighTideMeters() + tidal.getPredictedLowTideMeters()) / 2.0;
            double waterLevel = midLevel + (tidalRange / 2.0) * Math.sin(tidalAngle);
            // rate in cm/hour: derivative of sin is cos, scale by amplitude and angular frequency
            double tidalRate  = (tidalRange / 2.0) * Math.cos(tidalAngle)
                                * (2 * Math.PI / (TIDAL_PERIOD_MS / 1000.0)) * 3600.0;
            tidal.setWaterLevelMeters(waterLevel);
            tidal.setTidalRateCmPerHour(tidalRate);
            tidal.setTidalPhase(computeTidalPhase(tidalAngle));
            tidal.setNextTideChangeMinutes(computeNextTideChangeMinutes(tidalAngle));
        }

        // ── Atmospheric drift ─────────────────────────────────────────────────────
        AtmosphericData atm = buoy.getAtmospheric();
        if (atm != null) {
            atm.setCo2Ppm(clamp(atm.getCo2Ppm() + drift(0.02), 410.0, 430.0));
            atm.setBarometricPressureHpa(clamp(atm.getBarometricPressureHpa() + drift(0.3), 980.0, 1040.0));
            atm.setAirTempCelsius(clamp(atm.getAirTempCelsius() + drift(0.1), -5.0, 35.0));
            atm.setWindSpeedKnots(clamp(atm.getWindSpeedKnots() + drift(0.5), 0.0, 60.0));
            atm.setWindDirectionDegrees((atm.getWindDirectionDegrees() + drift(2.0) + 360.0) % 360.0);
            atm.setRelativeHumidityPercent(clamp(atm.getRelativeHumidityPercent() + drift(0.5), 30.0, 100.0));
            atm.setSolarIrradianceWm2(clamp(atm.getSolarIrradianceWm2() + drift(5.0), 0.0, 1100.0));
        }

        // ── Oceanographic drift ───────────────────────────────────────────────────
        OceanographicData ocean = buoy.getOceanographic();
        if (ocean != null) {
            ocean.setSurfaceTempCelsius(clamp(ocean.getSurfaceTempCelsius() + drift(0.05), -2.0, 30.0));
            // Subsurface is cooler and less variable than surface
            ocean.setSubsurfaceTempCelsius(clamp(ocean.getSubsurfaceTempCelsius() + drift(0.02),
                    -2.0, ocean.getSurfaceTempCelsius() - 0.2));
            ocean.setSalinityPsu(clamp(ocean.getSalinityPsu() + drift(0.01), 28.0, 38.0));
            ocean.setPhLevel(clamp(ocean.getPhLevel() + drift(0.005), 7.8, 8.5));
            ocean.setDissolvedOxygenMgL(clamp(ocean.getDissolvedOxygenMgL() + drift(0.05), 4.0, 14.0));
            ocean.setTurbidityNtu(clamp(ocean.getTurbidityNtu() + drift(0.05), 0.1, 20.0));
            ocean.setChlorophyllUgL(clamp(ocean.getChlorophyllUgL() + drift(0.02), 0.01, 20.0));
            ocean.setDissolvedCo2Umol(clamp(ocean.getDissolvedCo2Umol() + drift(0.1), 5.0, 50.0));
        }

        // ── Wave simulation — correlates with wind speed ───────────────────────────
        WaveData wave = buoy.getWave();
        if (wave != null && atm != null) {
            double targetWaveHeight = atm.getWindSpeedKnots() * 0.05;
            double currentHs        = wave.getSignificantWaveHeightMeters();
            double newHs            = clamp(currentHs + Math.signum(targetWaveHeight - currentHs) * 0.02
                                            + drift(0.01), 0.1, 15.0);
            wave.setSignificantWaveHeightMeters(newHs);
            wave.setMaxWaveHeightMeters(clamp(newHs * (1.5 + drift(0.05)), newHs, newHs * 2.0));
            wave.setPeakWavePeriodSeconds(clamp(wave.getPeakWavePeriodSeconds() + drift(0.1), 3.0, 25.0));
            wave.setMeanWavePeriodSeconds(clamp(wave.getMeanWavePeriodSeconds() + drift(0.05), 2.0, 20.0));
            wave.setWaveDirectionDegrees((wave.getWaveDirectionDegrees() + drift(2.0) + 360.0) % 360.0);
            wave.setSwellHeightMeters(clamp(wave.getSwellHeightMeters() + drift(0.02), 0.05, 8.0));
            wave.setSwellPeriodSeconds(clamp(wave.getSwellPeriodSeconds() + drift(0.05), 5.0, 30.0));
            wave.setSwellDirectionDegrees((wave.getSwellDirectionDegrees() + drift(1.0) + 360.0) % 360.0);
        }

        // ── Current drift ─────────────────────────────────────────────────────────
        CurrentData current = buoy.getCurrent();
        if (current != null) {
            current.setSurfaceCurrentSpeedKnots(clamp(current.getSurfaceCurrentSpeedKnots() + drift(0.02), 0.0, 5.0));
            current.setSurfaceCurrentDirectionDegrees(
                    (current.getSurfaceCurrentDirectionDegrees() + drift(1.0) + 360.0) % 360.0);
            current.setSubsurfaceCurrentSpeedKnots(
                    clamp(current.getSubsurfaceCurrentSpeedKnots() + drift(0.01), 0.0, 3.0));
            current.setSubsurfaceCurrentDirectionDegrees(
                    (current.getSubsurfaceCurrentDirectionDegrees() + drift(1.0) + 360.0) % 360.0);
            current.setVerticalCurrentCmPerSec(
                    clamp(current.getVerticalCurrentCmPerSec() + drift(0.01), -2.0, 2.0));
        }

        // ── Small buoy position drift (anchored, slight movement) ────────────────
        double driftAngle = random.nextDouble() * 2 * Math.PI;
        double driftDist  = random.nextDouble() * MAX_DRIFT_METERS;
        double newLat = buoy.getLatitude() + (driftDist * Math.sin(driftAngle)) / METERS_PER_DEGREE_LAT;
        double metersPerDegreeLon = METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(buoy.getLatitude()));
        double newLon = buoy.getLongitude() + (driftDist * Math.cos(driftAngle)) / metersPerDegreeLon;
        buoy.setLatitude(newLat);
        buoy.setLongitude(newLon);

        // ── Status evaluation ─────────────────────────────────────────────────────
        BuoyStatus newStatus = BuoyStatus.OPERATIONAL;
        if (ocean != null && (ocean.getPhLevel() < 7.9 || ocean.getDissolvedOxygenMgL() < 5.0)) {
            newStatus = BuoyStatus.DEGRADED;
        }
        if (random.nextDouble() < 0.001) {
            newStatus = BuoyStatus.MAINTENANCE;
        }
        buoy.setStatus(newStatus);
        buoy.setTimestamp(Instant.now());

        metricsCollector.record(buoy);
    }

    /** Returns a random delta in [-maxDelta, +maxDelta] */
    private double drift(double maxDelta) {
        return (random.nextDouble() * 2.0 - 1.0) * maxDelta;
    }

    /** Clamps value to [min, max] */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Determines tidal phase from the sine/cosine quadrant.
     * sin  > 0 and cos > 0 → RISING
     * sin  > 0 and cos < 0 → FALLING (after high)
     * sin  < 0 and cos < 0 → FALLING (continuing)
     * sin  < 0 and cos > 0 → RISING (after low)
     * Near zero crossings → HIGH_SLACK or LOW_SLACK
     */
    private String computeTidalPhase(double tidalAngle) {
        double sin = Math.sin(tidalAngle);
        double cos = Math.cos(tidalAngle);
        if (Math.abs(cos) < 0.05) {
            return sin > 0 ? "HIGH_SLACK" : "LOW_SLACK";
        }
        return cos > 0 ? "RISING" : "FALLING";
    }

    /**
     * Computes approximate minutes until the next slack-water event.
     * A slack event occurs when cos(angle) = 0, i.e., at angle = pi/2 + n*pi.
     */
    private int computeNextTideChangeMinutes(double tidalAngle) {
        // Normalize angle to [0, 2*pi)
        double normalised = tidalAngle % (2 * Math.PI);
        if (normalised < 0) normalised += 2 * Math.PI;
        // Next slack at pi/2 or 3*pi/2
        double nextSlack1 = Math.PI / 2;
        double nextSlack2 = 3 * Math.PI / 2;
        double distToNext;
        if (normalised < nextSlack1) {
            distToNext = nextSlack1 - normalised;
        } else if (normalised < nextSlack2) {
            distToNext = nextSlack2 - normalised;
        } else {
            distToNext = (2 * Math.PI - normalised) + nextSlack1;
        }
        double periodSeconds = TIDAL_PERIOD_MS / 1000.0;
        double secondsToNext = distToNext / (2 * Math.PI) * periodSeconds;
        return (int) (secondsToNext / 60.0);
    }

    public synchronized void resize(int targetCount) {
        int current = fleet.size();
        if (targetCount > current) {
            for (int i = current + 1; i <= targetCount; i++) {
                // Find a free index
                int index = i;
                while (fleet.containsKey(String.format("BUOY-%03d", index))) {
                    index++;
                }
                spawnBuoy(index);
            }
        } else if (targetCount < current) {
            Set<String> keys = fleet.keySet();
            int toRemove = current - targetCount;
            keys.stream()
                    .sorted()
                    .skip(targetCount)
                    .limit(toRemove)
                    .forEach(fleet::remove);
        }
        log.info("Fleet resized from {} to {} buoys (actual: {})", current, targetCount, fleet.size());
    }

    public Collection<BuoyPayload> getAll() {
        return fleet.values();
    }

    public BuoyPayload getById(String id) {
        return fleet.get(id);
    }

    public Set<String> getBuoyIds() {
        return fleet.keySet();
    }

    public int getCurrentBuoyCount() {
        return fleet.size();
    }
}
