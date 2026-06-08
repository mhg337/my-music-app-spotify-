package com.jbeat.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null) ? value : defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL 드라이버를 찾을 수 없습니다!", e);
        }

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");

        if (url == null) {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/jbeat?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8", "root", "mysql");
        }

        return DriverManager.getConnection(url, user, password);
    }
}