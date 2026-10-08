package com.vitalfit.model;

import java.sql.Timestamp;

public class Goal {
    private int id;
    private int userId;
    private String title;
    private double targetValue;
    private double currentValue;
    private String unit;
    private String status; // "IN_PROGRESS", "COMPLETED", "EXPIRED"
    private Timestamp createdAt;

    public Goal() {}

    public Goal(int id, int userId, String title, double targetValue, double currentValue, String unit, String status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.unit = unit;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getProgressPercentage() {
        if (targetValue <= 0) return 100.0;
        double pct = (currentValue / targetValue) * 100.0;
        return Math.min(100.0, Math.round(pct * 10.0) / 10.0);
    }
}
