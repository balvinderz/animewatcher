package com.stuffbox.webscraper.scrapers;

import android.util.Log;

import com.stuffbox.webscraper.models.Quality;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class XStreamScraper extends  Scraper {

    private final Document gogoAnimePageDocument;

    public XStreamScraper(Document gogoAnimePageDocument) {
        this.gogoAnimePageDocument = gogoAnimePageDocument;
    }
    @Override
    public ArrayList<Quality> getQualityUrls() {
        String xstreamLink = gogoAnimePageDocument.getElementsByClass("xstreamcdn").get(0).getElementsByTag("a").get(0).attr("data-video");
        Log.i("apiurl",xstreamLink);
        String id = xstreamLink.substring(xstreamLink.lastIndexOf("/")+1);
        String apiUrl = "https://fcdn.stream/api/source/"+id;
        Log.i("apiUrl","soja :"+ apiUrl);
        ArrayList<Quality> qualities  = new ArrayList<>();

        try {
          Document apiDocument =   Jsoup.connect(apiUrl).ignoreContentType(true).post();
            JSONObject jsonObject = new JSONObject(apiDocument.text());
           JSONArray datas =  jsonObject.getJSONArray("data");
           for(int i =0;i<datas.length();i++)
           {
               JSONObject data = datas.getJSONObject(i);
               String qualityString =  data.getString("label");
               String qualityUrl = data.getString("file");
               Quality quality = new Quality(qualityString,qualityUrl);
               qualities.add(quality);


           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qualities;
    }

    @Override
    public String getHost() {
        return "Exoplayer";
    }
}
