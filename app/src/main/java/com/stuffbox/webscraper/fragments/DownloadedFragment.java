package com.stuffbox.webscraper.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadCursor;
import com.google.android.exoplayer2.offline.DownloadIndex;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.adapters.AnimeDataAdapter;
import com.stuffbox.webscraper.adapters.DownloadedAnimeDataAdapter;
import com.stuffbox.webscraper.application.AnimeWatcherApplication;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadedFragment extends Fragment {
    private ArrayList<Anime> mAnimeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.animefinder,container,false);
        recyclerView = view.findViewById(R.id.recyclerview);
        swipeRefreshLayout=view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getAnimeList();
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        getAnimeList();
        super.onResume();
    }
    private void getAnimeList(){
        mAnimeList.clear();
//        SQLiteDatabase recent=Objects.requireNonNull(getContext()).openOrCreateDatabase("recent",Context.MODE_PRIVATE,null);
//        Cursor  resultSet = recent.rawQuery("Select * from anime",null);
//        resultSet.moveToLast();
//        for(int i=resultSet.getCount()-1;i>=0;i--) {
//            Anime anime =new Anime();
//
//            anime.setName(resultSet.getString(0));
//            anime.setEpisodeNo(resultSet.getString(1));
//            anime.setLink(resultSet.getString(2));
//            anime.setImageLink(resultSet.getString(3));
//            Log.d("imagelinkis","soja "+ anime.getImageLink());
//
//            mAnimeList.add(anime);
//
//            resultSet.move(-1);
//        }
//
        DownloadManager manager = ((AnimeWatcherApplication)( getActivity().getApplication())).getDownloadManager();
        try {
           DownloadCursor cursor =  manager.getDownloadIndex().getDownloads();
           cursor.moveToLast();
           for(int i=cursor.getCount()-1;i>=0;i--)
           {
               if(cursor.getDownload().state == Download.STATE_COMPLETED)
               {
                   String x = cursor.getDownload().request.id;
                   String[] split = x.split(";");
                   Anime anime = new Anime(split[0],split[1],split[2],split[3],"0");
                   mAnimeList.add(anime);
                   cursor.moveToPrevious();

               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DownloadedAnimeDataAdapter mDataAdapter = new DownloadedAnimeDataAdapter(getContext(), mAnimeList,(AnimeWatcherApplication)getActivity().getApplication(),getFragmentManager());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDataAdapter);
        swipeRefreshLayout.setRefreshing(false);

    }
}

