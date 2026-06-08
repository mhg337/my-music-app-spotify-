package com.jbeat.service.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LastFmSearchService {

    private static final String LAST_FM_API_KEY = "a465aaa5d498d1ccece12bb4c6ad5784";
    private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";

    public String[] searchTrackMetadata(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String apiUrl = BASE_URL + "?method=track.search&track=" + encodedQuery
                    + "&api_key=" + LAST_FM_API_KEY + "&format=json&limit=1";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) { sb.append(line); }
                br.close();

                String json = sb.toString();

                if (json.contains("\"trackmatches\":{\"track\":[]}")) {
                    System.out.println("❌ Last.fm 검색 결과 없음: " + query);
                    return null;
                }

                String nameKey = "\"name\":\"";
                String artistKey = "\"artist\":\"";

                int nameIdx = json.indexOf(nameKey);
                int artistIdx = json.indexOf(artistKey);

                if (nameIdx != -1 && artistIdx != -1) {
                    int nameStart = nameIdx + nameKey.length();
                    int nameEnd = json.indexOf("\"", nameStart);
                    String trackName = json.substring(nameStart, nameEnd);

                    int artistStart = artistIdx + artistKey.length();
                    int artistEnd = json.indexOf("\"", artistStart);
                    String artistName = json.substring(artistStart, artistEnd);

                    trackName = trackName.replace("\\/", "/");
                    artistName = artistName.replace("\\/", "/");

                    System.out.println("✨ Last.fm 정제 완료: [" + artistName + "] " + trackName);

                    return new String[]{trackName, artistName};
                }
            } else {
                System.out.println("❌ Last.fm 서버 통신 에러: HTTP " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}