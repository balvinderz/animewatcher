package com.stuffbox.webscraper.scrapers;

import android.util.Log;

import com.stuffbox.webscraper.models.Quality;


import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class NewScraper extends  Scraper{
    private Document gogoAnimePageDocument ;

    public NewScraper(Document gogoAnimePageDocument) {
        this.gogoAnimePageDocument = gogoAnimePageDocument;
    }

    @Override
    public ArrayList<Quality> getQualityUrls() {
            Log.i("newScraperRunning","running");
        String vidStreamUrl = gogoAnimePageDocument.getElementsByClass("play-video").get(0).getElementsByTag("iframe").get(0).attr("src");
        Log.i("vidsteramurl is",vidStreamUrl);
        vidStreamUrl = vidStreamUrl.replaceAll("streaming.php","ajax.php");
        try {
            Document page = Jsoup.connect(vidStreamUrl).ignoreContentType(true).get();
            JSONObject jsonObject = new JSONObject(page.text());
            String qualityUrl = ((JSONObject)jsonObject.getJSONArray("source_bk").get(0)).getString("file");
            String quality = "HD P";
            ArrayList<Quality> qualities  = new ArrayList<>();
            qualities.add(new Quality(quality,qualityUrl));
            return qualities;

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(vidStreamUrl);
        return new ArrayList<Quality>();

    }

    @Override
    public String getHost() {
        return "Exoplayer";
    }
}
