package com.vitalfit.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Challenge {
    private int id;
    private String title;
    private String description;
    private String targetType;
    private double targetGoal;
    private Date startDate;
    private Date endDate;
    private Timestamp createdAt;
    private int participantCount;
    private boolean isJoined;

    public Challenge() {}

    public Challenge(int id, String title, String description, String targetType, double targetGoal, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetType = targetType;
        this.targetGoal = targetGoal;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public double getTargetGoal() {
        return targetGoal;
    }

    public void setTargetGoal(double targetGoal) {
        this.targetGoal = targetGoal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }
}
