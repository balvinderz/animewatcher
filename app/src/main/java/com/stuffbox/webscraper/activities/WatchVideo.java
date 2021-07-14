package com.stuffbox.webscraper.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;
import com.stuffbox.webscraper.models.Quality;
import com.stuffbox.webscraper.scrapers.NewScraper;
import com.stuffbox.webscraper.scrapers.Option1;
import com.stuffbox.webscraper.scrapers.Option2;
import com.stuffbox.webscraper.scrapers.Scraper;
import com.stuffbox.webscraper.scrapers.XStreamScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WatchVideo extends AppCompatActivity {
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
    public static String url = "https://gogoanime.pe/";
    int currentScraper = 2;
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
    View.OnClickListener nextEpisodeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("igotcalled","igotcalled with episode number "+ episodeNumber);
            if (nextVideoLink == null || nextVideoLink.equals(""))
                Toast.makeText(getApplicationContext(), "Last Episode", Toast.LENGTH_SHORT).show();
            else {
                updateTimer.cancel();
                episodeNumber += 1;
                changedAnime = true;
                executeQuery(animeName, episodeNumber, nextVideoLink, imageLink);
                currentScraper = 3;
                player.setPlayWhenReady(false);
                changedEpisode = false;
                new ScrapeVideoLink(nextVideoLink, context).execute();
            }
        }
    };
    Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED && playWhenReady ) {
                if (nextVideoLink == null || nextVideoLink.equals(""))
                    Toast.makeText(getApplicationContext(), "Last Episode", Toast.LENGTH_SHORT).show();
                else {
                    changedEpisode = true;

                    updateTimer.cancel();
                    episodeNumber += 1;
                    changedAnime = true;
                    executeQuery(animeName, episodeNumber, nextVideoLink, imageLink);
                    currentScraper = 2;
                  //  player.setPlayWhenReady(false);
                    changedEpisode = false;
                    new ScrapeVideoLink(nextVideoLink, context).execute();
                }
            } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i("exoerror", error.getMessage());
            currentScraper--;
            if (currentScraper <0)
                useFallBack();
            else {
                link = tempGogoAnimeLink;
                changingScraper();
            }
            // useFallBack();
        }
    };
    View.OnClickListener previousEpisodeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (previousVideoLink == null || previousVideoLink.equals(""))
                Toast.makeText(getApplicationContext(), "First Episode", Toast.LENGTH_SHORT).show();
            else {
                updateTimer.cancel();
                changedAnime = true;
                episodeNumber -= 1;
                currentScraper = 2;
                player.setPlayWhenReady(false);
                executeQuery(animeName, episodeNumber, previousVideoLink, imageLink);

                new ScrapeVideoLink(previousVideoLink, context).execute();
            }
        }
    };

    View.OnClickListener qualityChangerOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            ArrayList<String> qualityInfo = new ArrayList<>();
            for (Quality quality : qualities)
                qualityInfo.add(quality.getQuality());
            AlertDialog.Builder builder = new AlertDialog.Builder(WatchVideo.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle("Quality")
                    .setItems(qualityInfo.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentQuality != which) {
                                long t = player.getCurrentPosition();
                                currentQuality = which;

                                DefaultHttpDataSourceFactory dataSourceFactory = getSettedHeadersDataFactory();
                                HlsMediaSource hlsMediaSource =
                                        new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(qualities.get(currentQuality).getQualityUrl()));
                                player.prepare(hlsMediaSource);
                                player.setPlayWhenReady(true);
                                player.seekTo(t);
                            }
                        }
                    });

            builder.show();
        }
    };
    private boolean changedEpisode = false;

    DefaultHttpDataSourceFactory getSettedHeadersDataFactory() {


        String userAgent = Util.getUserAgent(context, "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/532.5 (KHTML, like Gecko) Chrome/4.0.249.0 Safari/532.5");
        if (currentScraper == 0 || currentScraper >=2)
            return new DefaultHttpDataSourceFactory(userAgent);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        dataSourceFactory.getDefaultRequestProperties().set("Accept", "*/*");
        dataSourceFactory.getDefaultRequestProperties().set("Accept-Encoding", "gzip,deflate,br");
        dataSourceFactory.getDefaultRequestProperties().set("Accept-Language", "en-IN,en;q=0.9,ur-IN;q=0.8,ur;q=0.7,en-GB;q=0.6,en-US;q=0.5");
        dataSourceFactory.getDefaultRequestProperties().set("Connection", "keep-alive");
        dataSourceFactory.getDefaultRequestProperties().set("Origin", "https://vidstreaming.io");
        dataSourceFactory.getDefaultRequestProperties().set("Referer", "https://vidstreaming.io");
        dataSourceFactory.getDefaultRequestProperties().set("Sec-Fetch-Mode", "cors");
        dataSourceFactory.getDefaultRequestProperties().set("Sec-Fetch-Site", "cross-site");
        dataSourceFactory.getDefaultRequestProperties().set("User-Agent", userAgent);
        dataSourceFactory.getDefaultRequestProperties().set("Host", host);
        return dataSourceFactory;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoviewer);
        setVideoOptions();
        initUIElements();
        context = this;
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        animeDatabase = AnimeDatabase.getInstance(getApplicationContext());
        link = getIntent().getStringExtra("link");

        int lastIndexOfDash = link.lastIndexOf("-");
        episodeNumber = Integer.parseInt(link.substring(lastIndexOfDash + 1));
        animeName = getIntent().getStringExtra("animename");
        Log.i("linkis", link);
        imageLink = getIntent().getStringExtra("imagelink");
        // time = Long.parseLong(getIntent().getStringExtra("time"));
        currentAnime = animeDatabase.animeDao().getAnimeByNameAndEpisodeNo(animeName, String.valueOf(episodeNumber));
        Log.i("yotimertimer", "soja" + currentAnime.getTime());
        //   Log.i("yotimerepisode",String.valueOf(episodeNumber));

        // currentAnime.setTime(String.valueOf(time));

        new ScrapeVideoLink(link, this).execute();
        if (android.os.Build.VERSION.SDK_INT >= 26)
            mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();

    }

    void initUIElements() {
        playerView = findViewById(R.id.exoplayer);
        controls = findViewById(R.id.wholecontroller);
        progressBar = findViewById(R.id.buffer);
        title = findViewById(R.id.titleofanime);
        qualityChangerButton = findViewById(R.id.qualitychanger);
        nextEpisodeButton = findViewById(R.id.exo_nextvideo);
        previousEpisodeButton = findViewById(R.id.exo_prevvideo);
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

    void executeQuery(String animeName, int episodeNumber, String link, String imageLink) {
        Anime temp = animeDatabase.animeDao().getAnimeByNameAndEpisodeNo(animeName, String.valueOf(episodeNumber));
        String time = "0";
        if (temp != null)
            time = temp.getTime();

        animeDatabase.animeDao().deleteAnimeByNameAndEpisodeNo(animeName, String.valueOf(episodeNumber));
        Anime anime = new Anime(animeName, link, String.valueOf(episodeNumber), imageLink, time);
        animeDatabase.animeDao().insertAnime(anime);
        currentAnime = anime;


    }

    class ScrapeVideoLink extends AsyncTask<Void, Void, Void> {
        String gogoAnimeUrl;
        Context context;

        ScrapeVideoLink(String gogoAnimeUrl, Context context) {
            this.gogoAnimeUrl = gogoAnimeUrl;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tempGogoAnimeLink = gogoAnimeUrl;
            // player.setPlayWhenReady(false);
            title.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            scrapers.clear();
            qualities = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                DefaultHttpDataSourceFactory dataSourceFactory = getSettedHeadersDataFactory();
                Log.i("currentScraper",""+currentScraper);
                Log.i("currentlyplaying","soja"+  qualities.get(currentQuality).getQualityUrl());

                if(currentScraper<2)
                {
                    HlsMediaSource  hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(qualities.get(currentQuality).getQualityUrl()));
                    player.prepare(hlsMediaSource);

                }
                else
                {

                    MediaSource m = mediaSource(Uri.parse(qualities.get(currentQuality).getQualityUrl()),context);
                    player.prepare(m);

 }
                player.setPlayWhenReady(true);
//                if(changedAnime)
//                {
//                    changedAnime = false;
//                    player.seekTo(0);
//                }
//                else
                player.seekTo(Long.parseLong(currentAnime.getTime()));
                startedPlaying = true;
                updateTimer = new Timer();
                updateTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        // Enter your code


                        currentAnime.setTime(String.valueOf(player.getCurrentPosition()));
                        Log.i("yotimer", currentAnime.getTime());
                        animeDatabase.animeDao().updateAnime(currentAnime);
                    }
                }, 0, 2000);
            } catch (Exception e) {
              //  Log.i("exoerror", e.getMessage());
                //useFallBack();
            }
            try {
                player.removeListener(eventListener);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            player.addListener(eventListener);
            progressBar.setVisibility(View.GONE);
            title.setText(animeName + " Episode " + episodeNumber);
            nextEpisodeButton.setOnClickListener(nextEpisodeOnClickListener);
            previousEpisodeButton.setOnClickListener(previousEpisodeOnClickListener);
            qualityChangerButton.setOnClickListener(qualityChangerOnClickListener);
            title.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document gogoAnimePageDocument = null;
            Log.i("currentPlaying", gogoAnimeUrl);
            try {
                if (gogoAnimeUrl.equals("https://www1.gogoanimes.ai/ansatsu-kyoushitsu-tv--episode-1"))//edge case
                    gogoAnimeUrl = url + "ansatsu-kyoushitsu-tv--episode-1";


                gogoAnimePageDocument = Jsoup.connect(gogoAnimeUrl).get();
                vidStreamUrl = "https:" + gogoAnimePageDocument.getElementsByClass("play-video").get(0).getElementsByTag("iframe").get(0).attr("src");
                previousVideoLink = gogoAnimePageDocument.select("div[class=anime_video_body_episodes_l]").select("a").attr("abs:href");
                nextVideoLink = gogoAnimePageDocument.select("div[class=anime_video_body_episodes_r]").select("a").attr("abs:href");
                Option1 option1 = new Option1(gogoAnimePageDocument);
                Option2 option2 = new Option2(gogoAnimePageDocument);
                scrapers.add(option1);
                scrapers.add(option2);
                scrapers.add(new NewScraper(gogoAnimePageDocument));
                scrapers.add(new XStreamScraper(gogoAnimePageDocument));

                qualities = scrapers.get(currentScraper).getQualityUrls();
                if (qualities.size() == 0) {
                    currentScraper--;
                    if (currentScraper<0) {
                        useFallBack();
                    } else {
                        link = gogoAnimeUrl;
                        changingScraper();
                    }
                }
                host = scrapers.get(currentScraper).getHost();
                if(currentScraper==0)
                currentQuality = 0;
                else
                    currentQuality = qualities.size()-1;


            } catch (Exception e) {
                Log.i("gogoanimeerror", e.toString());
            }
            return null;
        }
    }


    @Override
    public void onUserLeaveHint() {
        if (android.os.Build.VERSION.SDK_INT >= 26 && player.getPlayWhenReady())
            try {
                backStack = "lost";
                int x = playerView.getPlayer().getPlayWhenReady() ? R.drawable.pip_pause : R.drawable.pip_play;
                updatePictureInPictureActions(x, "soja", 0, 0, null);
                enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode) {
            controls.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (intent == null)
                        return;
                    Log.i("sojaasd", "marja");

                    if (player.getPlayWhenReady()) {
                        player.setPlayWhenReady(false);
                        updatePictureInPictureActions(R.drawable.pip_play, "play", 0, 0, intent);
                    } else {
                        player.setPlayWhenReady(true);
                        updatePictureInPictureActions(R.drawable.pip_pause, "pause", 0, 0, intent);
                    }


                }
            };
            registerReceiver(receiver, new IntentFilter(ACTION_MEDIA_CONTROL));

        } else {

            title.setVisibility(View.VISIBLE);
            controls.setVisibility(View.VISIBLE);
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    void updatePictureInPictureActions(@DrawableRes int iconId, String title, int controlType, int requestCode, Intent newintent) {
        final ArrayList<RemoteAction> actions = new ArrayList<>();
        if (newintent == null)
            newintent = new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType);
        final PendingIntent intent =
                PendingIntent.getBroadcast(
                        WatchVideo.this,
                        requestCode,
                        newintent,
                        0);
        final Icon icon = Icon.createWithResource(
                WatchVideo.this, iconId);
        RemoteAction action = new RemoteAction(icon, title, title, intent);
        actions.add(action);
        mPictureInPictureParamsBuilder.setActions(actions);
        setPictureInPictureParams(mPictureInPictureParamsBuilder.build());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();

        }

    }

    //  LifeCycleEvents
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("videolink", qualities.get(currentQuality).getQualityUrl());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

        time = player.getCurrentPosition();
    }

    @Override
    public void onStop() {
        super.onStop();
        time = player.getCurrentPosition();
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

    void useFallBack() {
        player.release();
        Intent intent = new Intent(context, webvideo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("videostreamlink", vidStreamUrl);
        try {
            updateTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(intent);

        finish();
    }

    void changingScraper() {
        new ScrapeVideoLink(link, context).execute();
        changedScraper = true;
        if (startedPlaying)
            time = player.getCurrentPosition();
    }
    private MediaSource mediaSource(Uri uri, Context context) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer")).
                createMediaSource(uri);
    }

}