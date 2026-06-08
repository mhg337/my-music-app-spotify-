package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService;

import java.io.IOException;

public class SearchController implements HttpHandler {
    private SpotifyPlayerService playerService = new SpotifyPlayerService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("q=")) {
            String keyword = java.net.URLDecoder.decode(query.split("q=")[1], "UTF-8");

            String trackUri = playerService.dynamicSpotifySearch(keyword);

            if (trackUri != null) {
                exchange.sendResponseHeaders(200, trackUri.length());
                exchange.getResponseBody().write(trackUri.getBytes());
            } else {
                String notFoundMsg = "NOT_FOUND";
                exchange.sendResponseHeaders(200, notFoundMsg.length());
                exchange.getResponseBody().write(notFoundMsg.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(400, 0);
        }

        exchange.close();
    }
}