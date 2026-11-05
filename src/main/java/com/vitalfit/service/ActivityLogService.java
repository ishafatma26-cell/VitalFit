package com.vitalfit.service;

import com.vitalfit.config.DBConnection;
import com.vitalfit.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Dedicated Java Concurrency Service for non-blocking asynchronous activity/workout audit logging.
 * Managed thread pool executor with thread factory, lifecycle management, and CompletableFuture support.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
public class ActivityLogService {

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private int count = 1;
        @Override
        public synchronized Thread newThread(Runnable r) {
            Thread t = new Thread(r, "VitalFit-AsyncLogger-" + (count++));
            t.setDaemon(true);
            return t;
        }
    };

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, THREAD_FACTORY);

    public static CompletableFuture<Void> logAsync(String userEmail, String action, String details) {
        return CompletableFuture.runAsync(() -> {
            String workerThreadName = Thread.currentThread().getName();
            String sql = "INSERT INTO activity_log (user_email, action, details, thread_name) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userEmail);
                stmt.setString(2, action);
                stmt.setString(3, details);
                stmt.setString(4, workerThreadName);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Failed to write async activity log: " + e.getMessage());
            }
        }, EXECUTOR);
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

    public static ExecutorService getExecutor() {
        return EXECUTOR;
    }

    public static void shutdown() {
        if (!EXECUTOR.isShutdown()) {
            EXECUTOR.shutdown();
        }
    }
}
