package com.stuffbox.webscraper.scrapers;

import com.stuffbox.webscraper.models.Quality;

import java.util.ArrayList;

public abstract class Scraper {
    public abstract ArrayList<Quality> getQualityUrls();
    public abstract  String getHost();
}
