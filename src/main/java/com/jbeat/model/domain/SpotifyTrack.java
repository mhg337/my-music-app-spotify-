package com.jbeat.model.domain;

public class SpotifyTrack extends AudioContent {
    private String spotifyUri;
    private int playCount;

    public SpotifyTrack(String id, String title, String artist, String spotifyUri, int playCount) {
        super(id, title, artist);
        this.spotifyUri = spotifyUri;
        this.playCount = playCount;
    }

    @Override
    public void play() {
        System.out.println("Spotify API를 통해 재생 제어 명령 전송: " + this.title);
    }
    @Override
    public void stop() {
        System.out.println("Spotify 재생 정지");
    }
    @Override
    public void next() {
        System.out.println("Spotify 다음 곡");
    }

    public String getSpotifyUri() {
        return spotifyUri;
    }

    public int getPlayCount() {
        return playCount;
    }
}

