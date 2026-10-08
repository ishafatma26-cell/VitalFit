package com.vitalfit.dao;

import com.vitalfit.config.DBConnection;
import com.vitalfit.model.Goal;
import com.vitalfit.model.Workout;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO {

    public boolean addWorkout(Workout workout) throws SQLException {
        String sql = "INSERT INTO workouts (user_id, activity_type, duration_minutes, calories_burned, distance_km, workout_date, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, workout.getUserId());
            stmt.setString(2, workout.getActivityType());
            stmt.setInt(3, workout.getDurationMinutes());
            stmt.setInt(4, workout.getCaloriesBurned());
            stmt.setDouble(5, workout.getDistanceKm());
            stmt.setDate(6, workout.getWorkoutDate() != null ? workout.getWorkoutDate() : new Date(System.currentTimeMillis()));
            stmt.setString(7, workout.getNotes());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        workout.setId(rs.getInt(1));
                    }
                }
                updateUserGoals(conn, workout.getUserId());
                return true;
            }
        }
        return false;
    }

    public List<Workout> getWorkoutsByUserId(int userId) throws SQLException {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT id, user_id, activity_type, duration_minutes, calories_burned, distance_km, workout_date, notes, created_at " +
                     "FROM workouts WHERE user_id = ? ORDER BY workout_date DESC, id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Workout w = new Workout(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("activity_type"),
                            rs.getInt("duration_minutes"),
                            rs.getInt("calories_burned"),
                            rs.getDouble("distance_km"),
                            rs.getDate("workout_date"),
                            rs.getString("notes")
                    );
                    w.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(w);
                }
            }
        }
        return list;
    }

    public List<Workout> getAllWorkoutsWithUserNames() throws SQLException {
        List<Workout> list = new ArrayList<>();
        String sql = "SELECT w.id, w.user_id, u.name as user_name, w.activity_type, w.duration_minutes, w.calories_burned, " +
                     "w.distance_km, w.workout_date, w.notes, w.created_at " +
                     "FROM workouts w JOIN users u ON w.user_id = u.id ORDER BY w.workout_date DESC, w.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Workout w = new Workout(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("activity_type"),
                        rs.getInt("duration_minutes"),
                        rs.getInt("calories_burned"),
                        rs.getDouble("distance_km"),
                        rs.getDate("workout_date"),
                        rs.getString("notes")
                );
                w.setUserName(rs.getString("user_name"));
                w.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(w);
            }
        }
        return list;
    }

    public boolean deleteWorkout(int workoutId, int userId) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                updateUserGoals(conn, userId);
                return true;
            }
        }
        return false;
    }

    public List<Goal> getGoalsByUserId(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT id, user_id, title, target_value, current_value, unit, status, created_at FROM goals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Goal g = new Goal(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getDouble("target_value"),
                            rs.getDouble("current_value"),
                            rs.getString("unit"),
                            rs.getString("status")
                    );
                    g.setCreatedAt(rs.getTimestamp("created_at"));
                    goals.add(g);
                }
            }
        }
        return goals;
    }

    public boolean addGoal(Goal goal) throws SQLException {
        String sql = "INSERT INTO goals (user_id, title, target_value, current_value, unit, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getTitle());
            stmt.setDouble(3, goal.getTargetValue());
            stmt.setDouble(4, goal.getCurrentValue());
            stmt.setString(5, goal.getUnit());
            stmt.setString(6, goal.getStatus() != null ? goal.getStatus() : "IN_PROGRESS");
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        goal.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void updateUserGoals(Connection conn, int userId) throws SQLException {
        // Calculate total calories, distance, and workout count
        String calcSql = "SELECT COALESCE(SUM(calories_burned), 0) as total_cals, " +
                         "COALESCE(SUM(distance_km), 0) as total_dist, " +
                         "COUNT(*) as total_count FROM workouts WHERE user_id = ?";
        double totalCals = 0;
        double totalDist = 0;
        double totalCount = 0;

        try (PreparedStatement stmt = conn.prepareStatement(calcSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalCals = rs.getDouble("total_cals");
                    totalDist = rs.getDouble("total_dist");
                    totalCount = rs.getDouble("total_count");
                }
            }
        }

        // 1. Update current_value based on unit
        String updateValSql = "UPDATE goals SET current_value = CASE " +
                              "WHEN LOWER(unit) LIKE '%kcal%' OR LOWER(unit) LIKE '%cal%' THEN ? " +
                              "WHEN LOWER(unit) LIKE '%km%' OR LOWER(unit) LIKE '%dist%' THEN ? " +
                              "ELSE ? END WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateValSql)) {
            stmt.setDouble(1, totalCals);
            stmt.setDouble(2, totalDist);
            stmt.setDouble(3, totalCount);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }

        // 2. Update status based on the newly updated current_value
        String updateStatusSql = "UPDATE goals SET status = CASE WHEN current_value >= target_value THEN 'COMPLETED' ELSE 'IN_PROGRESS' END WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateStatusSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
