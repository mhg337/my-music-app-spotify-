package com.jbeat.repository;

import com.jbeat.model.domain.ListenLog;

import java.util.List;

public interface LogRepository {
    List<ListenLog> loadLogs();
}
