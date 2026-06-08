package com.jbeat.model.domain;

public class ChartResult {
    public int rank;
    public String title;
    public String artist;
    public String genre;

    public ChartResult(int rank, String title, String artist, String genre) {
        this.rank = rank;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }
}
