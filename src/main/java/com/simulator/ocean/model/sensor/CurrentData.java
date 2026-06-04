package com.simulator.ocean.model.sensor;

public class CurrentData {

    /** Surface current speed in knots */
    private double surfaceCurrentSpeedKnots;

    /** Surface current direction in degrees (direction current is flowing toward) */
    private double surfaceCurrentDirectionDegrees;

    /** Subsurface current speed at 10 m depth in knots */
    private double subsurfaceCurrentSpeedKnots;

    /** Subsurface current direction at 10 m depth in degrees */
    private double subsurfaceCurrentDirectionDegrees;

    /** Vertical current speed in cm/s (positive = upwelling, negative = downwelling) */
    private double verticalCurrentCmPerSec;

    public CurrentData() {}

    public CurrentData(double surfaceCurrentSpeedKnots, double surfaceCurrentDirectionDegrees,
                       double subsurfaceCurrentSpeedKnots, double subsurfaceCurrentDirectionDegrees,
                       double verticalCurrentCmPerSec) {
        this.surfaceCurrentSpeedKnots = surfaceCurrentSpeedKnots;
        this.surfaceCurrentDirectionDegrees = surfaceCurrentDirectionDegrees;
        this.subsurfaceCurrentSpeedKnots = subsurfaceCurrentSpeedKnots;
        this.subsurfaceCurrentDirectionDegrees = subsurfaceCurrentDirectionDegrees;
        this.verticalCurrentCmPerSec = verticalCurrentCmPerSec;
    }

    public double getSurfaceCurrentSpeedKnots() { return surfaceCurrentSpeedKnots; }
    public void setSurfaceCurrentSpeedKnots(double surfaceCurrentSpeedKnots) { this.surfaceCurrentSpeedKnots = surfaceCurrentSpeedKnots; }

    public double getSurfaceCurrentDirectionDegrees() { return surfaceCurrentDirectionDegrees; }
    public void setSurfaceCurrentDirectionDegrees(double surfaceCurrentDirectionDegrees) { this.surfaceCurrentDirectionDegrees = surfaceCurrentDirectionDegrees; }

    public double getSubsurfaceCurrentSpeedKnots() { return subsurfaceCurrentSpeedKnots; }
    public void setSubsurfaceCurrentSpeedKnots(double subsurfaceCurrentSpeedKnots) { this.subsurfaceCurrentSpeedKnots = subsurfaceCurrentSpeedKnots; }

    public double getSubsurfaceCurrentDirectionDegrees() { return subsurfaceCurrentDirectionDegrees; }
    public void setSubsurfaceCurrentDirectionDegrees(double subsurfaceCurrentDirectionDegrees) { this.subsurfaceCurrentDirectionDegrees = subsurfaceCurrentDirectionDegrees; }

    public double getVerticalCurrentCmPerSec() { return verticalCurrentCmPerSec; }
    public void setVerticalCurrentCmPerSec(double verticalCurrentCmPerSec) { this.verticalCurrentCmPerSec = verticalCurrentCmPerSec; }

    @Override
    public String toString() {
        return "{" +
                "\"surfaceCurrentSpeedKnots\":" + surfaceCurrentSpeedKnots +
                ",\"surfaceCurrentDirectionDegrees\":" + surfaceCurrentDirectionDegrees +
                ",\"subsurfaceCurrentSpeedKnots\":" + subsurfaceCurrentSpeedKnots +
                ",\"subsurfaceCurrentDirectionDegrees\":" + subsurfaceCurrentDirectionDegrees +
                ",\"verticalCurrentCmPerSec\":" + verticalCurrentCmPerSec +
                "}";
    }
}
