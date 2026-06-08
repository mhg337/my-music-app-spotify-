package com.jbeat.service.music;

import com.jbeat.model.domain.Genre;
import com.jbeat.model.domain.ListenLog;
import com.jbeat.model.domain.UserPreference;
import com.jbeat.repository.MemoryDatabase;

import java.util.List;

public class AnalyticsService {
    public UserPreference getTopPreference() {
        List<ListenLog> logs = MemoryDatabase.getInstance().getLogs();
        return new UserPreference(Genre.KPOP, 75.5);
    }
}
