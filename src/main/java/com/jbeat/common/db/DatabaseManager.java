package com.jbeat.common.db;

import com.jbeat.common.util.AppConfig;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static class Track {
        public int id;
        public String title;
        public String artist;
        public String uri;

        public Track(int id, String title, String artist, String uri) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            this.uri = uri;
        }
    }

    private static List<Track> trackList = new ArrayList<>();

    public static void loadDatabase() {
        try (BufferedReader br = new BufferedReader(new FileReader(AppConfig.CSV_FILE_PATH, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length == 4) {
                    int id = Integer.parseInt(data[0].trim());
                    String title = data[1].trim();
                    String artist = data[2].trim();
                    String uri = data[3].trim();

                    trackList.add(new Track(id, title, artist, uri));
                }
            }
            System.out.println("✅ MemoryDB 로드 완료: 총 " + trackList.size() + "곡의 데이터를 가져왔습니다.");

        } catch (Exception e) {
            System.out.println("❌ CSV 파일 읽기 실패: 경로가 맞는지 확인해 주세요. (" + e.getMessage() + ")");
        }
    }

    public static List<Track> getAllTracks() {
        return trackList;
    }
}