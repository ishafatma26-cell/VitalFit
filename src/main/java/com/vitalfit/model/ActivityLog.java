package com.vitalfit.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private String userEmail;
    private String action;
    private String details;
    private String threadName;
    private Timestamp timestamp;

    public ActivityLog() {}

    public ActivityLog(int id, String userEmail, String action, String details, String threadName, Timestamp timestamp) {
        this.id = id;
        this.userEmail = userEmail;
        this.action = action;
        this.details = details;
        this.threadName = threadName;
        this.timestamp = timestamp;
    }

    public ActivityLog(String userEmail, String action, String details, String threadName) {
        this.userEmail = userEmail;
        this.action = action;
        this.details = details;
        this.threadName = threadName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
