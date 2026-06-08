package com.jbeat.service;

import com.jbeat.domain.Playlist;
import com.jbeat.domain.Track;
import com.jbeat.repository.PlaylistRepository;
import com.jbeat.repository.TrackRepositoryClass;

import java.util.List;

public class PlaylistService {

    private final PlaylistRepository playlistRepository = new PlaylistRepository();
    private final TrackRepositoryClass trackRepository = new TrackRepositoryClass();

    public String getAllPlaylistsJson() {
        List<Playlist> playlists = playlistRepository.findAll();
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < playlists.size(); i++) {
            Playlist pl = playlists.get(i);
            if (i > 0) json.append(",");

            json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"tracks\":[", pl.id, pl.name.replace("\"", "\\\"")));

            List<Track> tracks = trackRepository.findByPlaylistId(pl.id);
            for (int j = 0; j < tracks.size(); j++) {
                Track t = tracks.get(j);
                if (j > 0) json.append(",");
                json.append(String.format("{\"title\":\"%s\",\"uri\":\"%s\",\"genre\":\"%s\"}",
                        t.title.replace("\"", "\\\""), t.uri, t.genre));
            }
            json.append("]}");
        }
        json.append("]");
        return json.toString();
    }

    public String createPlaylist(String name) {
        String id = String.valueOf(System.currentTimeMillis());
        Playlist newPlaylist = new Playlist(id, name);

        if (playlistRepository.save(newPlaylist)) {
            return id;
        }
        return "ERROR";
    }

    public boolean addTrack(String playlistId, String title, String uri, String genre) {
        Track newTrack = new Track(title, uri, genre);
        return trackRepository.save(playlistId, newTrack);
    }

    public boolean renamePlaylist(String id, String newName) {
        return playlistRepository.updateName(id, newName);
    }

    public boolean deletePlaylist(String id) {
        return playlistRepository.deleteById(id);
    }

    public boolean removeTrack(String playlistId, String uri) {
        return trackRepository.deleteByUri(playlistId, uri);
    }
}