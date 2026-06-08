package com.jbeat.controller;

import com.sun.net.httpserver.HttpServer;

public class ApiRouter {
    public static void route(HttpServer server) {
        server.createContext("/api/chart", new ChartController());
        server.createContext("/api/play", new PlayerController());
        server.createContext("/api/analytics", new AnalyticsController());
        server.createContext("/api/tracks", new TrackController());
        server.createContext("/api/pause", new PauseController());
        server.createContext("/api/next", new NextController());
        server.createContext("/api/queue", new QueueController());
        server.createContext("/api/radio", new RadioController());
        server.createContext("/api/resume", new ResumeController());
        server.createContext("/api/previous", new PreviousController());
        server.createContext("/api/search", new SearchController());
        server.createContext("/api/playlists", new PlaylistController());
        server.createContext("/api/playlists/create", new PlaylistController());
        server.createContext("/api/playlists/add", new PlaylistController());
        server.createContext("/api/playlists/rename", new PlaylistController());
        server.createContext("/api/playlists/delete", new PlaylistController());
        server.createContext("/api/playlists/removeTrack", new PlaylistController());
    }
}
