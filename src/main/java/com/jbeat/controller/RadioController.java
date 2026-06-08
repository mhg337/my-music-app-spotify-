package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService;
import java.io.IOException;

public class RadioController implements HttpHandler {
    private SpotifyPlayerService playerService = new SpotifyPlayerService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("uri=")) {
            String trackUri = "";
            String artist = "";

            for (String param : query.split("&")) {
                if (param.startsWith("uri=")) trackUri = param.substring(4);
                if (param.startsWith("artist=")) artist = param.substring(7);
            }

            if (artist != null && !artist.isEmpty()) {
                artist = java.net.URLDecoder.decode(artist, "UTF-8");
            } else {
                artist = "BTS";
            }

            playerService.playTrack(trackUri);
            playerService.smartAutoQueue(trackUri, artist);

            exchange.sendResponseHeaders(200, 0);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
        exchange.close();
    }
}