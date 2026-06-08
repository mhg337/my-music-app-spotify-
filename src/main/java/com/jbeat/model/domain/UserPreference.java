package com.jbeat.model.domain;

public class UserPreference {
    private Genre topGenre;
    private double ratio;

    public UserPreference(Genre topGenre, double ratio) {
        this.topGenre = topGenre;
        this.ratio = ratio;
    }

    public Genre getTopGenre() {
        return topGenre;
    }

    public double getRatio() {
        return ratio;
    }
}
