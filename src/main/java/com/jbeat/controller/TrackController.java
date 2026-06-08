package com.jbeat.controller;

import com.jbeat.common.db.DatabaseManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TrackController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        List<DatabaseManager.Track> tracks = DatabaseManager.getAllTracks();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < tracks.size(); i++) {
            DatabaseManager.Track t = tracks.get(i);
            jsonBuilder.append("{")
                    .append("\"id\":").append(t.id).append(",")
                    .append("\"title\":\"").append(t.title).append("\",")
                    .append("\"artist\":\"").append(t.artist).append("\",")
                    .append("\"uri\":\"").append(t.uri).append("\"")
                    .append("}");
            if (i < tracks.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        String jsonResponse = jsonBuilder.toString();
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}