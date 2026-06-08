package com.jbeat.service.music;
import com.jbeat.model.domain.*;
import com.jbeat.repository.MemoryDatabase;

public class TrackMergeService {
    public ChartResult mergeData(SpotifyTrack liveTrack, int rank) {
        LocalDummyTrack localData = MemoryDatabase.getInstance().getTrack(liveTrack.getId());
        String genreStr = (localData != null) ? localData.getGenre().name() : Genre.UNKNOWN.name();

        return new ChartResult(rank, liveTrack.getTitle(), liveTrack.getArtist(), genreStr);
    }
}

