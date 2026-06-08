package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService; // 👈 올려주신 코드와 동일한 서비스 사용

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PlayerStatusController implements HttpHandler {
    // 💡 컨트롤러 안에서 SpotifyPlayerService를 불러옵니다.
    private SpotifyPlayerService playerService = new SpotifyPlayerService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if ("/api/status".equals(path)) {
            handleStatus(exchange);
        } else if ("/api/seek".equals(path)) {
            handleSeek(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"경로를 찾을 수 없습니다.\"}");
        }
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        // ✨ SpotifyPlayerService에게 현재 시간과 총 길이를 물어봅니다.
        // (주의: 이 메서드들은 SpotifyPlayerService 클래스 안에 직접 만들어 주셔야 합니다)
        double currentTime = playerService.getCurrentTime();
        double duration = playerService.getTotalDuration();

        String jsonResponse = String.format("{\"currentTime\": %.1f, \"duration\": %.1f}", currentTime, duration);

        sendResponse(exchange, 200, jsonResponse);
    }

    private void handleSeek(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        double seekTime = 0.0;

        if (query != null && query.startsWith("time=")) {
            try {
                seekTime = Double.parseDouble(query.split("=")[1]);

                // ✨ 사용자가 재생 바를 넘기면 SpotifyPlayerService에게 해당 시간으로 가라고 명령합니다.
                playerService.seekTo(seekTime);

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"시간 형식이 올바르지 않습니다.\"}");
                return;
            }
        }

        sendResponse(exchange, 200, "{\"status\": \"success\"}");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}