package com.jbeat.repository;

import com.jbeat.model.domain.ListenLog;
import com.jbeat.model.domain.LocalDummyTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDatabase {
    private static MemoryDatabase instance = new MemoryDatabase();
    private HashMap<String, LocalDummyTrack> trackMap = new HashMap<>();
    private List<ListenLog> logList = new ArrayList<>();

    private MemoryDatabase() {
    }

    public static MemoryDatabase getInstance() {
        return instance;
    }

    public void init(TrackRepository trackRepo, LogRepository logRepo) {
        for (LocalDummyTrack t : trackRepo.loadAllTracks()) {
            trackMap.put(t.getId(), t);
        }
        logList.addAll(logRepo.loadLogs());
        System.out.println("MemoryDB 로드 완료");
    }

    public LocalDummyTrack getTrack(String id) {
        return trackMap.get(id);
    }

    public List<ListenLog> getLogs() {
        return logList;
    }
}
