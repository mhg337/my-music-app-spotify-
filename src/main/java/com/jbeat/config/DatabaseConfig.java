package com.jbeat.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null) ? value : defaultValue;
    }

    // DatabaseConfig.java의 getConnection 메서드
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL 드라이버를 찾을 수 없습니다!", e);
        }

        // 환경 변수 주입 (Render에 설정한 값들을 가져옵니다)
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");

        // 로컬 개발용(환경 변수가 없을 때)
        if (url == null) {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/my_database", "root", "비번");
        }

        return DriverManager.getConnection(url, user, password);
    }
}