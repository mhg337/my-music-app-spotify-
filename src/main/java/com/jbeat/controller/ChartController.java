package com.jbeat.controller;

import com.jbeat.common.util.JsonConverter;
import com.jbeat.service.music.ChartGenerationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;

public class ChartController implements HttpHandler {
    private ChartGenerationService chartService = new ChartGenerationService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String jsonResponse = JsonConverter.toJson(chartService.getTopChart());

        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}