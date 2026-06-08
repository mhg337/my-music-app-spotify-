package com.jbeat.repository;

import com.jbeat.config.DatabaseConfig;
import com.jbeat.domain.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TrackRepositoryClass {

    public boolean save(String playlistId, Track track) {
        String sql = "INSERT INTO tracks (playlist_id, title, uri, genre) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistId);
            pstmt.setString(2, track.title);
            pstmt.setString(3, track.uri);
            pstmt.setString(4, track.genre);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByUri(String playlistId, String uri) {
        String sql = "DELETE FROM tracks WHERE playlist_id = ? AND uri = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistId);
            pstmt.setString(2, uri);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Track> findByPlaylistId(String playlistId) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT title, uri, genre FROM tracks WHERE playlist_id = ? ORDER BY added_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tracks.add(new Track(rs.getString("title"), rs.getString("uri"), rs.getString("genre")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }
}