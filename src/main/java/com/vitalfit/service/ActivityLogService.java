package com.vitalfit.service;

import com.vitalfit.config.DBConnection;
import com.vitalfit.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityLogService {

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    public static void logAsync(String userEmail, String action, String details) {
        final String currentThreadName = Thread.currentThread().getName();
        executor.submit(() -> {
            String sql = "INSERT INTO activity_log (user_email, action, details, thread_name) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userEmail);
                stmt.setString(2, action);
                stmt.setString(3, details);
                stmt.setString(4, currentThreadName);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Failed to write async activity log: " + e.getMessage());
            }
        });
    }

    public static List<ActivityLog> getRecentLogs(int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT id, user_email, action, details, thread_name, timestamp FROM activity_log ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getString("user_email"),
                            rs.getString("action"),
                            rs.getString("details"),
                            rs.getString("thread_name"),
                            rs.getTimestamp("timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading activity logs: " + e.getMessage());
        }
        return logs;
    }

    public static void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
