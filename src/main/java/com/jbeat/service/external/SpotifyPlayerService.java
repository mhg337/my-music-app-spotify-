package com.jbeat.service.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpotifyPlayerService {
    private SpotifyAuthService authService = new SpotifyAuthService();

    private String getAvailableDeviceId(String token) {
        try {
            URL url = new URL("https://api.spotify.com/v1/me/player/devices");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

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
                System.out.println("📡 스포티파이 응답(연결된 기기 상태): " + json);

                int idIndex = json.indexOf("\"id\"");
                if (idIndex != -1) {
                    int colonIndex = json.indexOf(":", idIndex);
                    int startQuote = json.indexOf("\"", colonIndex);
                    int endQuote = json.indexOf("\"", startQuote + 1);
                    return json.substring(startQuote + 1, endQuote);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void playTrack(String trackUri) {
        try {
            String token = authService.getToken();
            if (token == null) {
                System.out.println("❌ 오류: 유효한 액세스 토큰이 없습니다.");
                return;
            }

            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) {
                System.out.println("❌ 오류: 켜져 있는 스포티파이 앱을 찾을 수 없습니다.");
                System.out.println("💡 해결책: PC 스포티파이 앱을 켜고 아무 노래나 1초만 재생하여 앱을 '깨워'주세요!");
                return;
            }

            System.out.println("🎯 타겟 기기 ID 확보 완료: " + deviceId);
            System.out.println("▶️ 스포티파이 원격 제어 명령 전송 중... (" + trackUri + ")");

            URL url = new URL("https://api.spotify.com/v1/me/player/play?device_id=" + deviceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = "{\"uris\": [\"" + trackUri + "\"]}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 204) {
                System.out.println("✅ 성공: 스포티파이 앱에서 음악 재생을 시작합니다! 🎵");
            } else {
                System.out.println("❌ 재생 실패: HTTP 에러 코드 " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void pauseTrack() {
        try {
            String token = authService.getToken();
            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) return;

            URL url = new URL("https://api.spotify.com/v1/me/player/pause?device_id=" + deviceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Length", "0");
            conn.setDoOutput(true);
            conn.getOutputStream().write(new byte[0]);

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("⏸️ 성공: 음악을 일시 정지합니다.");
            } else {
                System.out.println("❌ 일시 정지 실패! HTTP 에러 코드: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void nextTrack() {
        try {
            String token = authService.getToken();
            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) return;

            URL url = new URL("https://api.spotify.com/v1/me/player/next?device_id=" + deviceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Length", "0");

            conn.setDoOutput(true);
            conn.getOutputStream().write(new byte[0]);

            int responseCode = conn.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("⏸️ 성공: 음악 상태 변경 완료!");
            } else {
                System.out.println("❌ 실패! HTTP 에러 코드: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("❌ 다음 곡 처리 중 예외 발생!");
            e.printStackTrace();
        }
    }
    public void addQueue(String trackUri) {
        try {
            String token = authService.getToken();
            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) return;

            URL url = new URL("https://api.spotify.com/v1/me/player/queue?uri=" + trackUri + "&device_id=" + deviceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Length", "0");

            conn.setDoOutput(true);
            conn.getOutputStream().write(new byte[0]);

            int responseCode = conn.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("⏸️ 성공: 음악 상태 변경 완료!");
            } else {
                System.out.println("❌ 실패! HTTP 에러 코드: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void resumeTrack() {
        try {
            String token = authService.getToken();
            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) return;

            String base64Url = "aHR0cHM6Ly9hcGkuc3BvdGlmeS5jb20vdjEvbWUvcGxheWVyL3BsYXk=";
            String baseUrl = new String(java.util.Base64.getDecoder().decode(base64Url));
            URL url = new URL(baseUrl + "?device_id=" + deviceId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Length", "0");
            conn.setDoOutput(true);
            conn.getOutputStream().write(new byte[0]);

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("▶️ 성공: 음악을 다시 재생합니다.");
            } else {
                System.out.println("❌ 다시 재생 실패: HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void previousTrack() {
        try {
            String token = authService.getToken();
            String deviceId = getAvailableDeviceId(token);
            if (deviceId == null) return;

            String base64Url = "aHR0cHM6Ly9hcGkuc3BvdGlmeS5jb20vdjEvbWUvcGxheWVyL3ByZXZpb3Vz";
            String baseUrl = new String(java.util.Base64.getDecoder().decode(base64Url));
            URL url = new URL(baseUrl + "?device_id=" + deviceId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Length", "0");
            conn.setDoOutput(true);
            conn.getOutputStream().write(new byte[0]);

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("⏪ 성공: 이전 곡으로 돌아갑니다.");
            } else {
                System.out.println("❌ 이전 곡 실패: HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void smartAutoQueue(String trackUri, String artistName) {
        try {
            System.out.println("✨ [" + artistName + "] 로컬 스마트 믹스 탐색 시작... (Spotify 글로벌 검색 우회)");

            String[] localFallbackTracks = {
                    "spotify:track:4jVXIuzMvB1wOaUikA8yvD",
                    "spotify:track:2bgTY4UwhfBYhGT4HUYStN",
                    "spotify:track:3r8RuvgbX9s7ammBn07D3W",
                    "spotify:track:60dJU1gNCs2y3Fqj0211Qf",
                    "spotify:track:0V3wPSX9ygBnCm8psDIegu"};

            int count = 0;
            for (String fallbackUri : localFallbackTracks) {
                if (!fallbackUri.equals(trackUri)) {
                    addQueue(fallbackUri);
                    count++;
                }

                if (count >= 3) break;
            }

            System.out.println("✅ 로컬 예비 곡 " + count + "곡 연속 장전 완료!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String dynamicSpotifySearch(String keyword) {
        try {
            String token = authService.getToken();
            String encodedQuery = java.net.URLEncoder.encode(keyword, "UTF-8");

            String base64Url = "aHR0cHM6Ly9hcGkuc3BvdGlmeS5jb20vdjEvc2VhcmNo";
            String baseUrl = new String(java.util.Base64.getDecoder().decode(base64Url));

            String apiUrl = baseUrl + "?q=" + encodedQuery + "&type=track";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) { sb.append(line); }
                br.close();

                String json = sb.toString();

                String searchStr = "\"spotify:track:";
                int index = json.indexOf(searchStr);
                if (index != -1) {
                    int startIndex = index + 1;
                    int endIndex = json.indexOf("\"", startIndex);
                    String trackUri = json.substring(startIndex, endIndex);

                    System.out.println("✨ 실시간 스포티파이 검색 성공: [" + keyword + "] -> " + trackUri);
                    return trackUri;
                }
            } else {
                System.out.println("❌ 다이내믹 검색 실패: HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}