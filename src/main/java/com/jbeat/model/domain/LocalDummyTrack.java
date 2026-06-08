package com.jbeat.model.domain;

public class LocalDummyTrack extends AudioContent {
    private Genre genre;
    private int releaseYear;

    public LocalDummyTrack(String id, String title, String artist, Genre genre, int releaseYear) {
        super(id, title, artist);
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    @Override
    public void play() {
        System.out.println("로컬 더미 음원 재생(시뮬레이션): " + this.title);
    }

    @Override
    public void stop() {
    }

    @Override
    public void next() {
    }

    public Genre getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }
}
