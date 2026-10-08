package com.vitalfit.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Workout {
    private int id;
    private int userId;
    private String userName; // Convenient for leaderboards/admin view
    private String activityType;
    private int durationMinutes;
    private int caloriesBurned;
    private double distanceKm;
    private Date workoutDate;
    private String notes;
    private Timestamp createdAt;

    public Workout() {}

    public Workout(int id, int userId, String activityType, int durationMinutes, int caloriesBurned, double distanceKm, Date workoutDate, String notes) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.distanceKm = distanceKm;
        this.workoutDate = workoutDate;
        this.notes = notes;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
