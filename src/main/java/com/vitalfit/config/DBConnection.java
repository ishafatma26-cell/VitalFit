package com.vitalfit.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Thread-safe Database Connection Pool provider using HikariCP.
 * Supports configurable connection properties, leak detection, and test override.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
public class DBConnection {

    private static volatile HikariDataSource dataSource = null;
    private static String customJdbcUrl = null;
    private static String customUser = null;
    private static String customPassword = null;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("Database driver not found: " + ex.getMessage());
            }
        }
    }

    public static synchronized void setTestConnectionDetails(String url, String user, String password) {
        customJdbcUrl = url;
        customUser = user;
        customPassword = password;
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = null;
    }

    private static synchronized void initializeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        HikariConfig config = new HikariConfig();
        if (customJdbcUrl != null) {
            config.setJdbcUrl(customJdbcUrl);
            config.setUsername(customUser != null ? customUser : "sa");
            config.setPassword(customPassword != null ? customPassword : "");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
        } else {
            String host = System.getenv("DB_HOST");
            String dbName = System.getenv("DB_NAME");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            if (host == null || host.isBlank()) {
                host = "localhost";
            }
            if (dbName == null || dbName.isBlank()) {
                dbName = "vitalfit";
            }
            if (user == null || user.isBlank()) {
                user = "postgres";
            }
            if (password == null) {
                password = "postgres";
            }

            String jdbcUrl;
            if (host.contains("jdbc:")) {
                jdbcUrl = host;
            } else {
                jdbcUrl = "jdbc:postgresql://" + host + ":5432/" + dbName + "?sslmode=require";
            }

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(3);
        }

        config.setPoolName("VitalFit-HikariPool");
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializeDataSource();
        }
        return dataSource.getConnection();
    }

    public static synchronized void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }
}
