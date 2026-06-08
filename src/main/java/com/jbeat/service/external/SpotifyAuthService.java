package com.jbeat.service.external;

import com.jbeat.common.util.AppConfig;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SpotifyAuthService {
    private String currentAccessToken = null;
    private long tokenExpirationTime = 0;

    public String getToken() {
        if (currentAccessToken == null || System.currentTimeMillis() > tokenExpirationTime) {
            refreshAccessToken();
        }
        return currentAccessToken;
    }

    private void refreshAccessToken() {
        try {
            System.out.println("Spotify API: 리프레시 토큰으로 새 액세스 토큰 발급 요청 중...");
            URL url = new URL("https://accounts.spotify.com/api/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String auth = AppConfig.SPOTIFY_CLIENT_ID + ":" + AppConfig.SPOTIFY_CLIENT_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setDoOutput(true);

            String data = "grant_type=refresh_token&refresh_token=" + AppConfig.SPOTIFY_REFRESH_TOKEN;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                String json = response.toString();
                String key = "\"access_token\":\"";
                int start = json.indexOf(key) + key.length();
                int end = json.indexOf("\"", start);
                currentAccessToken = json.substring(start, end);

                tokenExpirationTime = System.currentTimeMillis() + (3000 * 1000);
                System.out.println("Spotify API: 새 액세스 토큰 발급 성공!");
            } else {
                System.out.println("토큰 발급 실패: HTTP 에러 코드 " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}