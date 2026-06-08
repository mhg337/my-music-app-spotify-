package com.jbeat.repository;

import com.jbeat.model.domain.LocalDummyTrack;

import java.util.List;

public interface TrackRepository {
    List<LocalDummyTrack> loadAllTracks();
}
