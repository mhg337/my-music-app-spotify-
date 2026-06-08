package com.jbeat.model.domain;

public class ListenLog {
    private String userId;
    private String trackId;
    private String timestamp;

    public ListenLog(String userId, String trackId, String timestamp) {
        this.userId = userId;
        this.trackId = trackId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
