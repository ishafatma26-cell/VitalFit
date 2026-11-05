package com.vitalfit.dao;

import com.vitalfit.config.DBConnection;
import com.vitalfit.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
public class UserDAO implements GenericDao<User, Integer> {

    @Override
    public Optional<User> findById(Integer id) throws SQLException {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(findById(id.intValue()));
    }

    @Override
    public List<User> findAll() throws SQLException {
        return getAllUsers();
    }

    @Override
    public boolean save(User entity) throws SQLException {
        if (entity == null) return false;
        String defaultPw = entity.getPasswordHash() != null ? entity.getPasswordHash() : "Default@123";
        if (!defaultPw.startsWith("$2a$")) {
            return registerUser(entity, defaultPw);
        }

        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, defaultPw);
            stmt.setString(4, entity.getRole() != null ? entity.getRole() : "USER");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(User entity) throws SQLException {
        if (entity == null || entity.getId() <= 0) return false;
        String sql = "UPDATE users SET name = ?, role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getRole() != null ? entity.getRole() : "USER");
            stmt.setInt(3, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        if (id == null) return false;
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean registerUser(User user, String plainPassword) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashed);
            stmt.setString(4, user.getRole() != null ? user.getRole() : "USER");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public User authenticate(String email, String plainPassword) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (BCrypt.checkpw(plainPassword, hash)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                hash,
                                rs.getString("role"),
                                rs.getTimestamp("created_at")
                        );
                    }
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return users;
    }

    public boolean updateUserRole(int userId, String role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserName(int userId, String name) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
