package com.jbeat.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null) ? value : defaultValue;
    }

    static String url = getEnvOrDefault("DB_URL", "jdbc:mysql://localhost:3306/my_database");
    static String user = getEnvOrDefault("DB_USER", "root");
    static String password = getEnvOrDefault("DB_PASS", "my_password");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}