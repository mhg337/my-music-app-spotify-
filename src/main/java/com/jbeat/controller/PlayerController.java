package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PlayerController implements HttpHandler {
    private SpotifyPlayerService playerService = new SpotifyPlayerService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("uri=")) {
            String trackUri = query.split("uri=")[1];

            playerService.playTrack(trackUri);

            String response = "{\"status\":\"playing\", \"uri\":\"" + trackUri + "\"}";
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } else {
            String response = "{\"error\":\"곡 URI가 없습니다.\"}";
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        }
        exchange.close();
    }
}