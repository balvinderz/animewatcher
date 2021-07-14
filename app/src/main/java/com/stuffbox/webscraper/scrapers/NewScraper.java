package com.stuffbox.webscraper.scrapers;

import android.util.Log;

import com.stuffbox.webscraper.models.Quality;


import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewScraper extends  Scraper{
    private Document gogoAnimePageDocument ;

    public NewScraper(Document gogoAnimePageDocument) {
        this.gogoAnimePageDocument = gogoAnimePageDocument;
    }

    @Override
    public ArrayList<Quality> getQualityUrls() {
            Log.i("newScraperRunning","running");
        String vidStreamUrl = gogoAnimePageDocument.getElementsByClass("play-video").get(0).getElementsByTag("iframe").get(0).absUrl("src");
        Log.i("vidsteramurl is",vidStreamUrl);
        ArrayList<Quality> qualities  = new ArrayList<>();

        vidStreamUrl = vidStreamUrl.replaceAll("streaming.php","loadserver.php");
        try {
            Document page = Jsoup.connect(vidStreamUrl).ignoreContentType(true).get();
//            JSONObject jsonObject = new JSONObject(page.text());
            for(Element  element: page.getElementsByTag("script")) {
                if(element.outerHtml().contains("playerInstance.setup"))
                {
                    Pattern pattern = Pattern.compile("\\[\\.*.*");
                    Matcher matcher = pattern.matcher(element.outerHtml());
                    if (matcher.find())
                    {
                        Log.i("matched text is",element.outerHtml().substring(matcher.start(),matcher.end()));
                        String text = element.outerHtml().substring(matcher.start(),matcher.end()).replace("file","'file'").replace("label","'label'");
                        JSONArray array = new JSONArray(text);
                        String url = array.getJSONObject(0).getString("file");
                        String label = array.getJSONObject(0).getString("label");
                        qualities.add(new Quality(label,url));
                    }
                }
            }
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
