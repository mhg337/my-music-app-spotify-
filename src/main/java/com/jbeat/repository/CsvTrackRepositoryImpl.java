package com.jbeat.repository;
import com.jbeat.model.domain.*;
import java.util.*;

public class CsvTrackRepositoryImpl implements TrackRepository {
    @Override
    public List<LocalDummyTrack> loadAllTracks() {
        List<LocalDummyTrack> list = new ArrayList<>();
        list.add(new LocalDummyTrack("t1", "Ditto", "NewJeans", Genre.KPOP, 2023));
        list.add(new LocalDummyTrack("t2", "Shape of You", "Ed Sheeran", Genre.POP, 2017));
        return list;
    }
}

