package com.jbeat.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.jbeat.service.PlaylistService;

import java.io.IOException;

public class PlaylistController implements HttpHandler {
    private PlaylistService playlistService = new PlaylistService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String response = "OK";

        try {
            if (path.equals("/api/playlists")) {
                response = playlistService.getAllPlaylistsJson();

            } else if (path.equals("/api/playlists/create") && query != null) {
                String name = java.net.URLDecoder.decode(query.split("name=")[1].split("&")[0], "UTF-8");
                response = playlistService.createPlaylist(name);

            } else if (path.equals("/api/playlists/add") && query != null) {
                String id = query.split("id=")[1].split("&")[0];
                String title = java.net.URLDecoder.decode(query.split("title=")[1].split("&")[0], "UTF-8");
                String uri = java.net.URLDecoder.decode(query.split("uri=")[1].split("&")[0], "UTF-8");
                String genre = java.net.URLDecoder.decode(query.split("genre=")[1].split("&")[0], "UTF-8");

                playlistService.addTrack(id, title, uri, genre);
            } else if (path.equals("/api/playlists/rename") && query != null) {
                String id = query.split("id=")[1].split("&")[0];
                String newName = java.net.URLDecoder.decode(query.split("newName=")[1].split("&")[0], "UTF-8");
                playlistService.renamePlaylist(id, newName);

            } else if (path.equals("/api/playlists/delete") && query != null) {
                String id = query.split("id=")[1].split("&")[0];
                playlistService.deletePlaylist(id);

            } else if (path.equals("/api/playlists/removeTrack") && query != null) {
                String id = query.split("id=")[1].split("&")[0];
                String uri = java.net.URLDecoder.decode(query.split("uri=")[1].split("&")[0], "UTF-8");
                playlistService.removeTrack(id, uri);
            }

        } catch (Exception e) {
            response = "ERROR";
        }

        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}