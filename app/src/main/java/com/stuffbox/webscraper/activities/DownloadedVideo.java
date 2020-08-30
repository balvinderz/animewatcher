package com.stuffbox.webscraper.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.application.AnimeWatcherApplication;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;
import com.stuffbox.webscraper.models.Quality;
import com.stuffbox.webscraper.scrapers.Scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class DownloadedVideo extends AppCompatActivity {
    PlayerView playerView;
    SimpleExoPlayer player;
    LinearLayout controls;
    ImageButton nextEpisodeButton, previousEpisodeButton, qualityChangerButton;
    ProgressBar progressBar;
    TextView title;
    String imageLink;
    boolean changedScraper = false;
    long time;
    AnimeDatabase animeDatabase;
    String nextVideoLink = null;
    String previousVideoLink = null;
    public static String url = "https://www1.gogoanimes.ai/";
    int currentScraper = 3;
    ArrayList<Scraper> scrapers = new ArrayList<>();
    boolean startedPlaying = false;
    Context context;
    ArrayList<Quality> qualities;
    String vidStreamUrl;
    int currentQuality;
    String link;
    BroadcastReceiver receiver;
    String host;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    private String animeName;
    int episodeNumber;
    boolean changedAnime = false;
    String backStack = "";
    Anime currentAnime;
    String tempGogoAnimeLink ;
    Timer updateTimer;
    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoviewer);
        animeName = getIntent().getStringExtra("animename");
        link = getIntent().getStringExtra("link");

        int lastIndexOfDash = link.lastIndexOf("-");
        episodeNumber = Integer.parseInt(link.substring(lastIndexOfDash + 1));
        setVideoOptions();
        initUIElements();
        context = this;

        player = ExoPlayerFactory.newSimpleInstance(this);

        playerView.setPlayer(player);
                CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(
                ((AnimeWatcherApplication) getApplication()).getDownloadCache(), new DefaultDataSourceFactory(context,"soja"));
        ProgressiveMediaSource mediaSource;
        try {
            DownloadManager manager = ((AnimeWatcherApplication) getApplication()).getDownloadManager();
            String id = getIntent().getStringExtra("contentId");
            Log.i("idis",id);

            Uri uri = manager.getDownloadIndex().getDownload(id).request.uri;

            mediaSource = new ProgressiveMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(uri);
            player.prepare(mediaSource);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }
    void setVideoOptions() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }


    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    void initUIElements() {
        playerView = findViewById(R.id.exoplayer);
        controls = findViewById(R.id.wholecontroller);
        progressBar = findViewById(R.id.buffer);
        title = findViewById(R.id.titleofanime);
        title.setText(animeName + " Episode " + episodeNumber);
        qualityChangerButton = findViewById(R.id.qualitychanger);
        nextEpisodeButton = findViewById(R.id.exo_nextvideo);
        previousEpisodeButton = findViewById(R.id.exo_prevvideo);
    }
    @Override
    public void onPause() {
        super.onPause();

      //  time = player.getCurrentPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
        //time = player.getCurrentPosition();
        player.setPlayWhenReady(false);

    }

    @Override
    public void onResume() {
        super.onResume();


        playerView.getPlayer().setPlayWhenReady(true);


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK) {
            playerView.getPlayer().release();
            if (backStack.equals("lost")) {

                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) {
                    List<ActivityManager.AppTask> tasks = am.getAppTasks();
                    if (tasks != null && tasks.size() > 1) {

                        tasks.get(0).setExcludeFromRecents(true);
                        tasks.get(1).moveToFront();
                    }
                }


            }
            try {
                updateTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onBackPressed();
        }
        return false;
    }
}


