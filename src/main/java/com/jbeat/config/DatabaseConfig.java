package com.jbeat.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    static String url = System.getenv("jdbc:postgresql://ep-weathered-darkness-aq4i3rey-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require");
    static String user = System.getenv("neondb_owner");
    static String password = System.getenv("npg_9zOsAYfCU1NX");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}