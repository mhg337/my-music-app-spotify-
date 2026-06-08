package com.jbeat.service;

import com.jbeat.domain.ChartTrack;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ChartService {

    public String getTop100ChartJson() {
        List<ChartTrack> chartList = new ArrayList<>();
        StringBuilder json = new StringBuilder("[");

        try {
            String url = "https://music.bugs.co.kr/chart";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            Elements rows = doc.select("table.list.trackList tbody tr");

            int rank = 1;
            for (Element row : rows) {
                if (rank > 100) break; // 100위까지만 자르기

                String title = row.select("p.title a").text().trim();
                String artist = row.select("p.artist a").first().text().trim();

                chartList.add(new ChartTrack(rank, title, artist));
                rank++;
            }

            for (int i = 0; i < chartList.size(); i++) {
                ChartTrack track = chartList.get(i);
                if (i > 0) json.append(",");

                json.append(String.format("{\"rank\":%d,\"title\":\"%s\",\"artist\":\"%s\"}",
                        track.rank,
                        track.title.replace("\"", "\\\""),
                        track.artist.replace("\"", "\\\"")));
            }

        } catch (Exception e) {
            System.out.println("차트 크롤링 중 에러 발생!");
            e.printStackTrace();
        }

        json.append("]");
        return json.toString();
    }
}