package com.vitalfit;

import com.vitalfit.config.DBConnection;
import com.vitalfit.config.TransactionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying ACID JDBC Transaction Management and rollback safety.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionManagerTest {

    @BeforeAll
    public void setupDb() throws Exception {
        String jdbcUrl = "jdbc:h2:mem:vitalfit_tx_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        DBConnection.setTestConnectionDetails(jdbcUrl, "sa", "");

        String schemaSql = new String(Files.readAllBytes(Paths.get("schema.sql")));
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        }
    }

    @Test
    @DisplayName("TransactionManager commits multi-step mutations successfully")
    public void testSuccessfulTransactionCommit() throws Exception {
        int initialCount = getRowCount("users");

        boolean result = TransactionManager.executeInTransaction(conn -> {
            String sql1 = "INSERT INTO users (name, email, password_hash, role) VALUES ('Tx User 1', 'tx1@vitalfit.demo', 'hash1', 'USER')";
            try (Statement s = conn.createStatement()) {
                s.executeUpdate(sql1);
            }

            String sql2 = "INSERT INTO users (name, email, password_hash, role) VALUES ('Tx User 2', 'tx2@vitalfit.demo', 'hash2', 'USER')";
            try (Statement s = conn.createStatement()) {
                s.executeUpdate(sql2);
            }
            return true;
        });

        assertTrue(result);
        assertEquals(initialCount + 2, getRowCount("users"));
    }

    @Test
    @DisplayName("TransactionManager rolls back all changes if an exception occurs mid-transaction")
    public void testTransactionRollbackOnException() {
        int initialCount = getRowCount("users");

        assertThrows(SQLException.class, () -> {
            TransactionManager.executeInTransaction(conn -> {
                String sql1 = "INSERT INTO users (name, email, password_hash, role) VALUES ('Rollback User', 'rollback@vitalfit.demo', 'hash', 'USER')";
                try (Statement s = conn.createStatement()) {
                    s.executeUpdate(sql1);
                }

                // Deliberately trigger SQL error (violating unique email or syntax error)
                String sqlErr = "INSERT INTO non_existing_table_vitalfit VALUES (1, 2)";
                try (Statement s = conn.createStatement()) {
                    s.executeUpdate(sqlErr);
                }
                return true;
            });
        });

        // Verify that 'Rollback User' was rolled back completely
        assertEquals(initialCount, getRowCount("users"));
        assertFalse(userExists("rollback@vitalfit.demo"));
    }

    private int getRowCount(String tableName) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            fail("Failed to get row count: " + e.getMessage());
        }
        return 0;
    }

    private boolean userExists(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE email = ?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
