package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class QueueController implements HttpHandler {
    private SpotifyPlayerService playerService = new SpotifyPlayerService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("uri=")) {
            String trackUri = query.split("uri=")[1].trim();

            playerService.addQueue(trackUri);

            String response = "{\"status\":\"queued\"}";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
        exchange.close();
    }
}