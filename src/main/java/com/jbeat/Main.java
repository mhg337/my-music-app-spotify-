package com.jbeat;

import com.jbeat.model.domain.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Spotify App 시작 ===\n");

        // 1. Spotify 트랙 생성
        SpotifyTrack spotifyTrack = new SpotifyTrack(
            "track001",
            "Butter",
            "BTS",
            "spotify:track:3d3d3d",
            150000
        );

        // 2. 로컬 더미 트랙 생성
        LocalDummyTrack localTrack = new LocalDummyTrack(
            "local001",
            "우리의 밤은 아름다워",
            "Various Artists",
            Genre.KPOP,
            2023
        );

        // 3. 재생 테스트
        System.out.println("--- Spotify 트랙 ---");
        spotifyTrack.play();
        System.out.println("제목: " + spotifyTrack.getTitle());
        System.out.println("아티스트: " + spotifyTrack.getArtist());
        spotifyTrack.next();
        spotifyTrack.stop();

        System.out.println("\n--- 로컬 트랙 ---");
        localTrack.play();
        System.out.println("제목: " + localTrack.getTitle());
        System.out.println("장르: " + localTrack.getGenre());
        localTrack.stop();

        // 4. 리스닝 로그 생성
        System.out.println("\n--- 리스닝 로그 ---");
        ListenLog log = new ListenLog(
            "user123",
            "track001",
            "2026-05-31 10:30:00"
        );
        System.out.println("사용자ID: " + log.getUserId());
        System.out.println("트랙ID: " + log.getTrackId());
        System.out.println("재생시간: " + log.getTimestamp());

        // 5. 사용자 선호도 생성
        System.out.println("\n--- 사용자 선호도 ---");
        UserPreference preference = new UserPreference(Genre.KPOP, 0.45);
        System.out.println("선호 장르: " + preference.getTopGenre());
        System.out.println("선호도: " + (preference.getRatio() * 100) + "%");

        // 6. 차트 결과 생성
        System.out.println("\n--- 차트 결과 ---");
        ChartResult chart = new ChartResult(1, "Butter", "BTS", "KPOP");
        System.out.println("순위: " + chart.rank);
        System.out.println("제목: " + chart.title);
        System.out.println("아티스트: " + chart.artist);

        System.out.println("\n=== 프로그램 종료 ===");
    }
}

