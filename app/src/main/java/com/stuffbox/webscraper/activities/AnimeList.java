package com.stuffbox.webscraper.activities;

import android.os.Bundle;

import androidx.core.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.adapters.AnimeListAdapter;
import com.stuffbox.webscraper.models.Anime;

public class AnimeList extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ArrayList<Anime> animeList= new ArrayList<>();
    AnimeListAdapter mDataAdapter;
    public static int loadImages=0; // only load Images when user is searching
    Toolbar toolbar;
    RecyclerView recyclerView;
    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animelistrecyclerview);
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
            }
        });
        try
        {
            // Load json file
            InputStream is = getResources().openRawResource(R.raw.animelist);
            String s = IOUtils.toString(is);
            IOUtils.closeQuietly(is);
            JSONArray jsonArray=new JSONArray(s);
      for(int i=0;i<jsonArray.length();i++)
      {
          JSONObject a=jsonArray.getJSONObject(i);
          Anime anime = new Anime();

          JSONObject anim=a.getJSONObject("anime");
          anime.setLink((String) anim.get("link"));
          anime.setName((String) anim.get("Anime name"));
          anime.setImageLink((String) anim.get("imagelink"));
          animeList.add(anime);
      }
                   }
                   catch (Exception e) {
            e.printStackTrace();
        }
         recyclerView=findViewById(R.id.animelistrecyclerview);
           mDataAdapter = new AnimeListAdapter(getApplicationContext(), animeList,AnimeList.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDataAdapter);
    }
    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.animelist, menu);
        MenuItem search=menu.findItem(R.id.anime_list_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText=newText.toLowerCase();
        if(newText.length()==0) {
            loadImages = 0;

        }
        else
            loadImages=1;
        ArrayList<Anime> newAnimeList=new ArrayList<>();
        for(int i=0;i<animeList.size();i++)
        {
            if(animeList.get(i).getName().toLowerCase().contains(newText))
            newAnimeList.add(animeList.get(i));
        }
        mDataAdapter.setFilter(newAnimeList);

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right); // Animation
    }
}

