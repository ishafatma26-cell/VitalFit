package com.vitalfit.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

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

    public static void setTestConnectionDetails(String url, String user, String password) {
        customJdbcUrl = url;
        customUser = user;
        customPassword = password;
    }

    public static Connection getConnection() throws SQLException {
        if (customJdbcUrl != null) {
            return DriverManager.getConnection(customJdbcUrl, customUser, customPassword);
        }

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

        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}
