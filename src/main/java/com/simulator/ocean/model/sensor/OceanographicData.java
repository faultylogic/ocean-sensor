package com.simulator.ocean.model.sensor;

public class OceanographicData {

    /** Surface water temperature in Celsius */
    private double surfaceTempCelsius;

    /** Subsurface water temperature at 10 m depth (typically cooler than surface) */
    private double subsurfaceTempCelsius;

    /** Practical salinity units (approximately 32-35 PSU) */
    private double salinityPsu;

    /** Ocean pH (approximately 8.0-8.3) */
    private double phLevel;

    /** Dissolved oxygen in mg/L */
    private double dissolvedOxygenMgL;

    /** Water turbidity in NTU */
    private double turbidityNtu;

    /** Chlorophyll-a concentration in µg/L (algae proxy) */
    private double chlorophyllUgL;

    /** Dissolved CO2 in µmol/kg */
    private double dissolvedCo2Umol;

    public OceanographicData() {}

    public OceanographicData(double surfaceTempCelsius, double subsurfaceTempCelsius,
                              double salinityPsu, double phLevel, double dissolvedOxygenMgL,
                              double turbidityNtu, double chlorophyllUgL, double dissolvedCo2Umol) {
        this.surfaceTempCelsius = surfaceTempCelsius;
        this.subsurfaceTempCelsius = subsurfaceTempCelsius;
        this.salinityPsu = salinityPsu;
        this.phLevel = phLevel;
        this.dissolvedOxygenMgL = dissolvedOxygenMgL;
        this.turbidityNtu = turbidityNtu;
        this.chlorophyllUgL = chlorophyllUgL;
        this.dissolvedCo2Umol = dissolvedCo2Umol;
    }

    public double getSurfaceTempCelsius() { return surfaceTempCelsius; }
    public void setSurfaceTempCelsius(double surfaceTempCelsius) { this.surfaceTempCelsius = surfaceTempCelsius; }

    public double getSubsurfaceTempCelsius() { return subsurfaceTempCelsius; }
    public void setSubsurfaceTempCelsius(double subsurfaceTempCelsius) { this.subsurfaceTempCelsius = subsurfaceTempCelsius; }

    public double getSalinityPsu() { return salinityPsu; }
    public void setSalinityPsu(double salinityPsu) { this.salinityPsu = salinityPsu; }

    public double getPhLevel() { return phLevel; }
    public void setPhLevel(double phLevel) { this.phLevel = phLevel; }

    public double getDissolvedOxygenMgL() { return dissolvedOxygenMgL; }
    public void setDissolvedOxygenMgL(double dissolvedOxygenMgL) { this.dissolvedOxygenMgL = dissolvedOxygenMgL; }

    public double getTurbidityNtu() { return turbidityNtu; }
    public void setTurbidityNtu(double turbidityNtu) { this.turbidityNtu = turbidityNtu; }

    public double getChlorophyllUgL() { return chlorophyllUgL; }
    public void setChlorophyllUgL(double chlorophyllUgL) { this.chlorophyllUgL = chlorophyllUgL; }

    public double getDissolvedCo2Umol() { return dissolvedCo2Umol; }
    public void setDissolvedCo2Umol(double dissolvedCo2Umol) { this.dissolvedCo2Umol = dissolvedCo2Umol; }

    @Override
    public String toString() {
        return "{" +
                "\"surfaceTempCelsius\":" + surfaceTempCelsius +
                ",\"subsurfaceTempCelsius\":" + subsurfaceTempCelsius +
                ",\"salinityPsu\":" + salinityPsu +
                ",\"phLevel\":" + phLevel +
                ",\"dissolvedOxygenMgL\":" + dissolvedOxygenMgL +
                ",\"turbidityNtu\":" + turbidityNtu +
                ",\"chlorophyllUgL\":" + chlorophyllUgL +
                ",\"dissolvedCo2Umol\":" + dissolvedCo2Umol +
                "}";
    }
}
