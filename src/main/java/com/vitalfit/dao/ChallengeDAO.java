package com.vitalfit.dao;

import com.vitalfit.config.DBConnection;
import com.vitalfit.model.Challenge;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChallengeDAO {

    public List<Challenge> getAllChallenges(int currentUserId) throws SQLException {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description, c.target_type, c.target_goal, c.start_date, c.end_date, c.created_at, " +
                     "(SELECT COUNT(*) FROM challenge_participants cp WHERE cp.challenge_id = c.id) as part_count, " +
                     "EXISTS(SELECT 1 FROM challenge_participants cp2 WHERE cp2.challenge_id = c.id AND cp2.user_id = ?) as is_joined " +
                     "FROM challenges c ORDER BY c.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Challenge ch = new Challenge(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("target_type"),
                            rs.getDouble("target_goal"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date")
                    );
                    ch.setCreatedAt(rs.getTimestamp("created_at"));
                    ch.setParticipantCount(rs.getInt("part_count"));
                    ch.setJoined(rs.getBoolean("is_joined"));
                    challenges.add(ch);
                }
            }
        }
        return challenges;
    }

    public Set<Integer> getUserChallengeIds(int userId) throws SQLException {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT challenge_id FROM challenge_participants WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    set.add(rs.getInt("challenge_id"));
                }
            }
        }
        return set;
    }

    public Challenge getChallengeById(int challengeId) throws SQLException {
        String sql = "SELECT id, title, description, target_type, target_goal, start_date, end_date, created_at FROM challenges WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, challengeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Challenge ch = new Challenge(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("target_type"),
                            rs.getDouble("target_goal"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date")
                    );
                    ch.setCreatedAt(rs.getTimestamp("created_at"));
                    return ch;
                }
            }
        }
        return null;
    }

    public boolean deleteChallenge(int challengeId) throws SQLException {
        String sql = "DELETE FROM challenges WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, challengeId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * ACID Transaction for Joining a Challenge.
     * Sets autoCommit(false), checks duplicate entry, inserts participant, records activity log,
     * and commits transaction. In case of error, executes rollback.
     */
    public boolean joinChallenge(int userId, int challengeId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // 1. Verify user email for audit logging
            String userEmail = null;
            String userSql = "SELECT email FROM users WHERE id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setInt(1, userId);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userEmail = rs.getString("email");
                    }
                }
            }

            if (userEmail == null) {
                conn.rollback();
                return false;
            }

            // 2. Duplicate Check
            String checkSql = "SELECT id FROM challenge_participants WHERE challenge_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, challengeId);
                checkStmt.setInt(2, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // User already joined this challenge
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Insert Participant Record
            String insertSql = "INSERT INTO challenge_participants (challenge_id, user_id, status) VALUES (?, ?, 'ACTIVE')";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, challengeId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
            }

            // 4. Log Activity as part of transaction
            String logSql = "INSERT INTO activity_log (user_email, action, details, thread_name) VALUES (?, ?, ?, ?)";
            try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                logStmt.setString(1, userEmail);
                logStmt.setString(2, "JOIN_CHALLENGE");
                logStmt.setString(3, "Successfully joined challenge ID " + challengeId);
                logStmt.setString(4, Thread.currentThread().getName());
                logStmt.executeUpdate();
            }

            conn.commit(); // Commit ACID Transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    public boolean createChallenge(Challenge challenge) throws SQLException {
        String sql = "INSERT INTO challenges (title, description, target_type, target_goal, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, challenge.getTitle());
            stmt.setString(2, challenge.getDescription());
            stmt.setString(3, challenge.getTargetType());
            stmt.setDouble(4, challenge.getTargetGoal());
            stmt.setDate(5, challenge.getStartDate());
            stmt.setDate(6, challenge.getEndDate());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        challenge.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
