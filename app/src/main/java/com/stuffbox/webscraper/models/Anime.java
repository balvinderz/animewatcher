package com.stuffbox.webscraper.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "anime")
public class Anime {
    @PrimaryKey(autoGenerate = true)
    public int id ;
   @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "link")
    String  link;
    @ColumnInfo(name = "episodeNo")
    String  episodeNo;
    @ColumnInfo(name = "imageLink")
    String imageLink;
    @ColumnInfo(name = "time")

    String time;

    @Ignore
    public Anime(String name, String link, String imageLink) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.imageLink = imageLink;
        episodeNo="";
        this.time = "0";

    }

    public Anime(int id ,String name, String link, String episodeno, String imageLink,String time) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.episodeNo = episodeno;
        this.imageLink = imageLink;
        this.time =time;
    }
    @Ignore
    public Anime(String name, String link, String episodeno, String imageLink,String time) {
        this.name = name;
        this.link = link;
        this.episodeNo = episodeno;
        this.imageLink = imageLink;
        this.time =time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Anime()
    {

    }

    public String getEpisodeNo() {
        return episodeNo;
    }

    public void setEpisodeNo(String episodeNo) {
        this.episodeNo = episodeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
