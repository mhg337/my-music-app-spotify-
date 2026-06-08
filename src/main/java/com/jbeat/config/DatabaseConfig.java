package com.jbeat.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null) ? value : defaultValue;
    }


    static String url = getEnvOrDefault("DB_URL", "jdbc:mysql://localhost:3306/jbeat?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8");
    static String user = getEnvOrDefault("DB_USER", "root");
    static String password = getEnvOrDefault("DB_PASS", "mysql");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}