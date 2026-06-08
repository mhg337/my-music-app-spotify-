package com.jbeat.controller;

import com.jbeat.common.util.JsonConverter;
import com.jbeat.service.music.AnalyticsService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class AnalyticsController implements HttpHandler {
    private AnalyticsService analyticsService = new AnalyticsService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        String json = JsonConverter.toJson(analyticsService.getTopPreference());
        exchange.sendResponseHeaders(200, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.close();
    }
}
