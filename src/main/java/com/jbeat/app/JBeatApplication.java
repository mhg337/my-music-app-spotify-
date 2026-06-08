package com.jbeat.app;

import com.jbeat.common.db.DatabaseManager;
import com.jbeat.common.util.AppConfig;
import com.jbeat.controller.HttpServerManager;
import com.jbeat.controller.PlaylistController;
import com.jbeat.repository.*;

public class JBeatApplication {
    public static void main(String[] args) {
        System.out.println("=== J-Beat 시스템 부팅 중 ===");

        MemoryDatabase db = MemoryDatabase.getInstance();
        db.init(new CsvTrackRepositoryImpl(), new TxtLogRepositoryImpl());

        DatabaseManager.loadDatabase();

        try {
            HttpServerManager serverManager = new HttpServerManager();
            serverManager.startServer(AppConfig.SERVER_PORT);
            System.out.println("J-Beat 서버가 포트 " + AppConfig.SERVER_PORT + "에서 성공적으로 시작되었습니다.");
        } catch (Exception e) {
            System.out.println("❌ 서버 구동 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}