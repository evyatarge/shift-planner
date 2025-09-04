package com.example.shiftplanner.domain;

public class SchedulingSettings {
    private int minRestHours;      // מינימום שעות מנוחה בין משמרות
    private String restMode;       // "HARD" או "SOFT"

    public SchedulingSettings() {}
    public SchedulingSettings(int minRestHours, String restMode) {
        this.minRestHours = minRestHours;
        this.restMode = restMode;
    }

    public int getMinRestHours() { return minRestHours; }
    public void setMinRestHours(int minRestHours) { this.minRestHours = minRestHours; }
    public String getRestMode() { return restMode; }
    public void setRestMode(String restMode) { this.restMode = restMode; }

    public boolean isHardMode() { return "HARD".equalsIgnoreCase(restMode); }
    public boolean isSoftMode() { return "SOFT".equalsIgnoreCase(restMode) || restMode == null; }
}