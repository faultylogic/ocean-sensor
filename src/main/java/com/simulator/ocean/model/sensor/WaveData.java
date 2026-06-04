package com.simulator.ocean.model.sensor;

public class WaveData {

    /** Significant wave height Hs — mean of highest 1/3 of waves in meters */
    private double significantWaveHeightMeters;

    /** Maximum individual wave height recorded in meters */
    private double maxWaveHeightMeters;

    /** Peak wave period Tp in seconds */
    private double peakWavePeriodSeconds;

    /** Mean wave period Tm in seconds */
    private double meanWavePeriodSeconds;

    /** Primary wave direction in degrees (direction waves are coming from) */
    private double waveDirectionDegrees;

    /** Swell wave height in meters */
    private double swellHeightMeters;

    /** Swell wave period in seconds */
    private double swellPeriodSeconds;

    /** Swell direction in degrees (direction swell is coming from) */
    private double swellDirectionDegrees;

    public WaveData() {}

    public WaveData(double significantWaveHeightMeters, double maxWaveHeightMeters,
                    double peakWavePeriodSeconds, double meanWavePeriodSeconds,
                    double waveDirectionDegrees, double swellHeightMeters,
                    double swellPeriodSeconds, double swellDirectionDegrees) {
        this.significantWaveHeightMeters = significantWaveHeightMeters;
        this.maxWaveHeightMeters = maxWaveHeightMeters;
        this.peakWavePeriodSeconds = peakWavePeriodSeconds;
        this.meanWavePeriodSeconds = meanWavePeriodSeconds;
        this.waveDirectionDegrees = waveDirectionDegrees;
        this.swellHeightMeters = swellHeightMeters;
        this.swellPeriodSeconds = swellPeriodSeconds;
        this.swellDirectionDegrees = swellDirectionDegrees;
    }

    public double getSignificantWaveHeightMeters() { return significantWaveHeightMeters; }
    public void setSignificantWaveHeightMeters(double significantWaveHeightMeters) { this.significantWaveHeightMeters = significantWaveHeightMeters; }

    public double getMaxWaveHeightMeters() { return maxWaveHeightMeters; }
    public void setMaxWaveHeightMeters(double maxWaveHeightMeters) { this.maxWaveHeightMeters = maxWaveHeightMeters; }

    public double getPeakWavePeriodSeconds() { return peakWavePeriodSeconds; }
    public void setPeakWavePeriodSeconds(double peakWavePeriodSeconds) { this.peakWavePeriodSeconds = peakWavePeriodSeconds; }

    public double getMeanWavePeriodSeconds() { return meanWavePeriodSeconds; }
    public void setMeanWavePeriodSeconds(double meanWavePeriodSeconds) { this.meanWavePeriodSeconds = meanWavePeriodSeconds; }

    public double getWaveDirectionDegrees() { return waveDirectionDegrees; }
    public void setWaveDirectionDegrees(double waveDirectionDegrees) { this.waveDirectionDegrees = waveDirectionDegrees; }

    public double getSwellHeightMeters() { return swellHeightMeters; }
    public void setSwellHeightMeters(double swellHeightMeters) { this.swellHeightMeters = swellHeightMeters; }

    public double getSwellPeriodSeconds() { return swellPeriodSeconds; }
    public void setSwellPeriodSeconds(double swellPeriodSeconds) { this.swellPeriodSeconds = swellPeriodSeconds; }

    public double getSwellDirectionDegrees() { return swellDirectionDegrees; }
    public void setSwellDirectionDegrees(double swellDirectionDegrees) { this.swellDirectionDegrees = swellDirectionDegrees; }

    @Override
    public String toString() {
        return "{" +
                "\"significantWaveHeightMeters\":" + significantWaveHeightMeters +
                ",\"maxWaveHeightMeters\":" + maxWaveHeightMeters +
                ",\"peakWavePeriodSeconds\":" + peakWavePeriodSeconds +
                ",\"meanWavePeriodSeconds\":" + meanWavePeriodSeconds +
                ",\"waveDirectionDegrees\":" + waveDirectionDegrees +
                ",\"swellHeightMeters\":" + swellHeightMeters +
                ",\"swellPeriodSeconds\":" + swellPeriodSeconds +
                ",\"swellDirectionDegrees\":" + swellDirectionDegrees +
                "}";
    }
}
