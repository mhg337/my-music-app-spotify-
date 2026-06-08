package com.jbeat.controller;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.external.SpotifyPlayerService;
import java.io.IOException;

public class PauseController implements HttpHandler {
    private SpotifyPlayerService playerService = new SpotifyPlayerService();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        playerService.pauseTrack();
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }
}