package com.stuffbox.webscraper.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.adapters.AnimeDataAdapter;
import com.stuffbox.webscraper.models.Anime;

public class AnimeFragment extends Fragment {
    private String url;
    private ArrayList<Anime> AnimeList=new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    int initial=1;
 public    static AnimeFragment newInstance(String url)
   {
       Bundle bundle=new Bundle();
       bundle.putString("url",url);
       AnimeFragment dubFragment=new AnimeFragment();
        dubFragment.setArguments(bundle);
        return  dubFragment;
   }
   private void readBundle(Bundle bundle)
   {
       if(bundle!=null)
       {
           url=bundle.getString("url");

       }
   }



    private ProgressBar progressBar;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.dublayout,container,false);
        swipeRefreshLayout=view.findViewById(R.id.swiperefresh);

            new Dub().execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Dub().execute();
            }
        });
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class Dub extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            readBundle(getArguments());
            if(initial==1) {
                progressBar = view.findViewById(R.id.progress);
                progressBar.setVisibility(View.VISIBLE);
                //     swipeRefreshLayout.setRefreshing(true);
            }
            }

        @Override
        protected Void  doInBackground(Void... params) {
            try {
                org.jsoup.nodes.Document searching;
                searching  = Jsoup.connect(url).get();
                Elements li=searching.select("div[class=last_episodes loaddub]").select("ul[class=items]").select("li");
                int size = li.size();
                for (int i = 0; i < size; i++) {
                    Anime anime = new Anime();

                    Elements mElementAnimeName=searching.select("p[class=name]").select("a").eq(i);
                    String mAnimenName= mElementAnimeName.text();
                    if(mAnimenName.contains("[email protect  ed]"))
                        mAnimenName= mAnimenName.replace("[email protected]","IDOLM@STER");
                    String mlink=searching.select("p[class=name]").select("a").eq(i).attr("abs:href");
                    String imagelink=searching.select("div[class=img]").select("img").eq(2*i).attr("src");
                    String episodeno=searching.select("p[class=episode]").eq(i).text();
                    int index = episodeno.indexOf(" ");
                    episodeno = episodeno.substring(index+1);
                    anime.setName(mAnimenName);
                    Log.i("imagelinkis",imagelink);
                    anime.setLink(mlink);
                    anime.setEpisodeNo(episodeno);
                    anime.setImageLink(imagelink);
                    AnimeList.add(anime);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            RecyclerView mRecyclerView = view.findViewById(R.id.act_recyclerview);
            AnimeDataAdapter mDataAdapter = new AnimeDataAdapter(view.getContext(), AnimeList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            mRecyclerView.setItemViewCacheSize(20);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);
            initial=0;
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
