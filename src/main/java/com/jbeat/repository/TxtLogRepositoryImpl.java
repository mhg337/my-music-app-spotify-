package com.jbeat.repository;

import com.jbeat.model.domain.ListenLog;

import java.util.ArrayList;
import java.util.List;

public class TxtLogRepositoryImpl implements LogRepository {
    @Override
    public List<ListenLog> loadLogs() {
        List<ListenLog> logs = new ArrayList<>();
        logs.add(new ListenLog("user1", "t1", "2026-05-01"));
        return logs;
    }
}
