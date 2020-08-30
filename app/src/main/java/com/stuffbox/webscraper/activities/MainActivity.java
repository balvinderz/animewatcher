package com.stuffbox.webscraper.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.adapters.SearchAdapter;
import com.stuffbox.webscraper.adapters.ViewPagerAdapter;
import com.stuffbox.webscraper.constants.Constants;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout noanime;
    String searchurl;
    AppBarLayout appBarLayout;
    private ArrayList<Anime> mAnimeList = new ArrayList<>();
    MenuItem prevMenuItem;
    RecyclerView recyclerView;

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    SearchAdapter DataAdapter;
    SearchingAnime x = new SearchingAnime();
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences=getSharedPreferences("settings",0);
        playTuturuSound(preferences);
        if(!haveNetworkConnection(getApplicationContext()))
        {
            LinearLayout linearLayout1=findViewById(R.id.notvisiblelinearlayout);
            linearLayout1.setVisibility(View.VISIBLE);
            viewPager=findViewById(R.id.viewPager);
            viewPager.setVisibility(View.GONE);
            bottomNavigationView=findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
             appBarLayout=findViewById(R.id.appbar);
           appBarLayout.setVisibility(View.GONE);
        }else{

        bottomNavigationView=findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.DUB:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.SUB:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.recent:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.download :
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                            return false;
                        }
                    });
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        noanime=findViewById(R.id.noanime);
        ViewPagerAdapter viewPagerAdapter;
        viewPager =  findViewById(R.id.viewPager);
        AnimeDatabase animeDatabase = AnimeDatabase.getInstance(this);
        progressBar=findViewById(R.id.progress2);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(1);
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (prevMenuItem != null)
                        prevMenuItem.setChecked(false);
                    else
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);

                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                    prevMenuItem = bottomNavigationView.getMenu().getItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.drawer, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        MenuItem animelist = menu.findItem(R.id.animelist);
        animelist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(getApplicationContext(), AnimeList.class);
                startActivity(i);
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
                return false;
            }
        });
        MenuItem settings=menu.findItem(R.id.settings);
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(getApplicationContext(),Settings.class);
                startActivity(intent);
                return false;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                noanime.setVisibility(View.GONE);

                if (newText.length() >= 3) {
                    RecyclerView recyclerView = findViewById(R.id.recyclerview2);
                    recyclerView.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    searchurl = Constants.url+"/search.html?keyword=" + newText;
                    if (x.getStatus() == AsyncTask.Status.RUNNING)
                        x.cancel(true);
                    x = new SearchingAnime();
                    x.execute();

                }
                else
                 {
                    if (x.getStatus() == AsyncTask.Status.RUNNING)
                        x.cancel(true);
                    RecyclerView recyclerView = findViewById(R.id.recyclerview2);
                    recyclerView.setVisibility(View.GONE);
                    progressBar=findViewById(R.id.progress2);

                     viewPager.setVisibility(View.VISIBLE);
                     bottomNavigationView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                return false;
            }
        });
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class SearchingAnime extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView = findViewById(R.id.recyclerview2);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                org.jsoup.nodes.Document searching = Jsoup.connect(searchurl).get();
                DataAdapter = new SearchAdapter();
                DataAdapter.notifyItemRangeRemoved(0, mAnimeList.size());
                mAnimeList.clear();

                Elements li = searching.select("div[class=main_body]").select("div[class=last_episodes]").select("ul[class=items]").select("li");
                for (int i = 0; i < li.size(); i++) {
                    Anime anime = new Anime();
                    anime.setLink(li.select("div[class=img]").eq(i).select("a").attr("abs:href"));
                    anime.setName(li.select("div[class=img]").eq(i).select("a").attr("title"));
                    anime.setImageLink(li.select("div[class=img]").eq(i).select("img").attr("src"));
                    mAnimeList.add(anime);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            if (mAnimeList.size() == 0)
                noanime.setVisibility(View.VISIBLE);
            else {
                recyclerView.setVisibility(View.VISIBLE);

                DataAdapter = new SearchAdapter(getApplicationContext(), mAnimeList,MainActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                recyclerView.setItemViewCacheSize(30);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(DataAdapter);
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!(viewPager.getCurrentItem() == 1) && viewPager.getVisibility()==View.VISIBLE)
                viewPager.setCurrentItem(1);
            else
                super.onBackPressed();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
    void playTuturuSound(SharedPreferences preferences)
    {
        if(!(preferences.contains("playtuturu")))

        {   SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("playtuturu",1);
            editor.apply();
        }
        if(preferences.getInt("playtuturu",0)==1)
        {
            final MediaPlayer mediaPlayer= MediaPlayer.create(this,R.raw.tuturu);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }

            });
        }
    }
     boolean haveNetworkConnection(android.content.Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
