package com.simulator.ocean.model.sensor;

public class TidalData {

    /** Water level height above chart datum in meters */
    private double waterLevelMeters;

    /** Rate of tidal change in cm/hour (positive = rising, negative = falling) */
    private double tidalRateCmPerHour;

    /** Current tidal phase: RISING, FALLING, HIGH_SLACK, LOW_SLACK */
    private String tidalPhase;

    /** Predicted height of the next high tide in meters */
    private double predictedHighTideMeters;

    /** Predicted height of the next low tide in meters */
    private double predictedLowTideMeters;

    /** Minutes until the next tidal phase change (slack water) */
    private int nextTideChangeMinutes;

    /** Tidal range: difference between high and low tide in meters */
    private double tidalRangeMeters;

    public TidalData() {}

    public TidalData(double waterLevelMeters, double tidalRateCmPerHour, String tidalPhase,
                     double predictedHighTideMeters, double predictedLowTideMeters,
                     int nextTideChangeMinutes, double tidalRangeMeters) {
        this.waterLevelMeters = waterLevelMeters;
        this.tidalRateCmPerHour = tidalRateCmPerHour;
        this.tidalPhase = tidalPhase;
        this.predictedHighTideMeters = predictedHighTideMeters;
        this.predictedLowTideMeters = predictedLowTideMeters;
        this.nextTideChangeMinutes = nextTideChangeMinutes;
        this.tidalRangeMeters = tidalRangeMeters;
    }

    public double getWaterLevelMeters() { return waterLevelMeters; }
    public void setWaterLevelMeters(double waterLevelMeters) { this.waterLevelMeters = waterLevelMeters; }

    public double getTidalRateCmPerHour() { return tidalRateCmPerHour; }
    public void setTidalRateCmPerHour(double tidalRateCmPerHour) { this.tidalRateCmPerHour = tidalRateCmPerHour; }

    public String getTidalPhase() { return tidalPhase; }
    public void setTidalPhase(String tidalPhase) { this.tidalPhase = tidalPhase; }

    public double getPredictedHighTideMeters() { return predictedHighTideMeters; }
    public void setPredictedHighTideMeters(double predictedHighTideMeters) { this.predictedHighTideMeters = predictedHighTideMeters; }

    public double getPredictedLowTideMeters() { return predictedLowTideMeters; }
    public void setPredictedLowTideMeters(double predictedLowTideMeters) { this.predictedLowTideMeters = predictedLowTideMeters; }

    public int getNextTideChangeMinutes() { return nextTideChangeMinutes; }
    public void setNextTideChangeMinutes(int nextTideChangeMinutes) { this.nextTideChangeMinutes = nextTideChangeMinutes; }

    public double getTidalRangeMeters() { return tidalRangeMeters; }
    public void setTidalRangeMeters(double tidalRangeMeters) { this.tidalRangeMeters = tidalRangeMeters; }

    @Override
    public String toString() {
        return "{" +
                "\"waterLevelMeters\":" + waterLevelMeters +
                ",\"tidalRateCmPerHour\":" + tidalRateCmPerHour +
                ",\"tidalPhase\":\"" + tidalPhase + "\"" +
                ",\"predictedHighTideMeters\":" + predictedHighTideMeters +
                ",\"predictedLowTideMeters\":" + predictedLowTideMeters +
                ",\"nextTideChangeMinutes\":" + nextTideChangeMinutes +
                ",\"tidalRangeMeters\":" + tidalRangeMeters +
                "}";
    }
}
