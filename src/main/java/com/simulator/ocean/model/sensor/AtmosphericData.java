package com.simulator.ocean.model.sensor;

public class AtmosphericData {

    /** Atmospheric CO2 concentration at surface (approximately 415-425 ppm) */
    private double co2Ppm;

    /** Barometric pressure (approximately 1013 hPa) */
    private double barometricPressureHpa;

    /** Air temperature in Celsius */
    private double airTempCelsius;

    /** Wind speed in knots */
    private double windSpeedKnots;

    /** Wind direction in degrees (0-360, meteorological convention) */
    private double windDirectionDegrees;

    /** Relative humidity percentage */
    private double relativeHumidityPercent;

    /** Solar irradiance in W/m² */
    private double solarIrradianceWm2;

    public AtmosphericData() {}

    public AtmosphericData(double co2Ppm, double barometricPressureHpa, double airTempCelsius,
                           double windSpeedKnots, double windDirectionDegrees,
                           double relativeHumidityPercent, double solarIrradianceWm2) {
        this.co2Ppm = co2Ppm;
        this.barometricPressureHpa = barometricPressureHpa;
        this.airTempCelsius = airTempCelsius;
        this.windSpeedKnots = windSpeedKnots;
        this.windDirectionDegrees = windDirectionDegrees;
        this.relativeHumidityPercent = relativeHumidityPercent;
        this.solarIrradianceWm2 = solarIrradianceWm2;
    }

    public double getCo2Ppm() { return co2Ppm; }
    public void setCo2Ppm(double co2Ppm) { this.co2Ppm = co2Ppm; }

    public double getBarometricPressureHpa() { return barometricPressureHpa; }
    public void setBarometricPressureHpa(double barometricPressureHpa) { this.barometricPressureHpa = barometricPressureHpa; }

    public double getAirTempCelsius() { return airTempCelsius; }
    public void setAirTempCelsius(double airTempCelsius) { this.airTempCelsius = airTempCelsius; }

    public double getWindSpeedKnots() { return windSpeedKnots; }
    public void setWindSpeedKnots(double windSpeedKnots) { this.windSpeedKnots = windSpeedKnots; }

    public double getWindDirectionDegrees() { return windDirectionDegrees; }
    public void setWindDirectionDegrees(double windDirectionDegrees) { this.windDirectionDegrees = windDirectionDegrees; }

    public double getRelativeHumidityPercent() { return relativeHumidityPercent; }
    public void setRelativeHumidityPercent(double relativeHumidityPercent) { this.relativeHumidityPercent = relativeHumidityPercent; }

    public double getSolarIrradianceWm2() { return solarIrradianceWm2; }
    public void setSolarIrradianceWm2(double solarIrradianceWm2) { this.solarIrradianceWm2 = solarIrradianceWm2; }

    @Override
    public String toString() {
        return "{" +
                "\"co2Ppm\":" + co2Ppm +
                ",\"barometricPressureHpa\":" + barometricPressureHpa +
                ",\"airTempCelsius\":" + airTempCelsius +
                ",\"windSpeedKnots\":" + windSpeedKnots +
                ",\"windDirectionDegrees\":" + windDirectionDegrees +
                ",\"relativeHumidityPercent\":" + relativeHumidityPercent +
                ",\"solarIrradianceWm2\":" + solarIrradianceWm2 +
                "}";
    }
}
