package com.jbeat.model.domain;

public abstract class AudioContent implements Playable {
    protected String id;
    protected String title;
    protected String artist;

    public AudioContent(String id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
