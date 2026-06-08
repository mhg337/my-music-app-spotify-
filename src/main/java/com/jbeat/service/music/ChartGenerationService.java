package com.jbeat.service.music;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ChartGenerationService {

    public List<Map<String, Object>> getTopChart() {
        List<Map<String, Object>> chartList = new ArrayList<>();

        try {
            String url = "https://music.bugs.co.kr/chart";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements rows = doc.select("table.list.trackList tbody tr");

            int rank = 1;
            for (Element row : rows) {
                if (rank > 100) break;

                String title = row.select("p.title a").text().trim();
                String artist = row.select("p.artist a").first().text().trim();

                Map<String, Object> trackInfo = new HashMap<>();
                trackInfo.put("rank", rank);
                trackInfo.put("title", title);
                trackInfo.put("artist", artist);

                chartList.add(trackInfo);
                rank++;
            }
        } catch (Exception e) {
            System.out.println("차트 크롤링 에러!");
            e.printStackTrace();
        }

        return chartList;
    }
}