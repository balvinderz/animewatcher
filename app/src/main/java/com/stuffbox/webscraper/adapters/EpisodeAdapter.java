package com.stuffbox.webscraper.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stuffbox.webscraper.Downloader;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.activities.WatchVideo;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;


public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.MyViewHolder> {
    private ArrayList<String> mSiteLink;
    private ArrayList<String> mEpisodeList;
    String animename;
    Activity activity;
    private Context context;
    private String imagelink;

    public EpisodeAdapter(Context context, ArrayList<String> SiteList, ArrayList<String> EpisodeList, String imagelink, String animename, Activity activity) {
        this.mSiteLink = SiteList;
        this.context = context;
        this.animename = animename;
        this.mEpisodeList = EpisodeList;
        this.imagelink = imagelink;
        this.activity = activity;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView button;
        private Button download;
        private LinearLayout layout;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.linearlayouta);
            button = view.findViewById(R.id.notbutton);
            download = view.findViewById(R.id.downloadchoice);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterforepisode, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.button.setText(animename + " Episode " + (position + 1));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WatchVideo.class);
                intent.putExtra("link", mSiteLink.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AnimeDatabase database = AnimeDatabase.getInstance(context);

                Anime temp = new Anime(animename, mSiteLink.get(position), String.valueOf(position + 1),imagelink,"0");
                Anime databaseAnime = database.animeDao().getAnimeByNameAndEpisodeNo(temp.getName(), temp.getEpisodeNo());

                String time = "0";
                if (databaseAnime != null) {
                    time = databaseAnime.getTime();
                }
                database.animeDao().deleteAnimeByNameAndEpisodeNo(temp.getName(), temp.getEpisodeNo());
                Anime anime = new Anime(temp.getName(), temp.getLink(), temp.getEpisodeNo(), temp.getImageLink(), time);
                database.animeDao().insertAnime(anime);
                intent.putExtra("animename", animename);
                intent.putExtra("imagelink", imagelink);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("noofepisodes", String.valueOf(mEpisodeList.size()));
                intent.putExtra("animenames", animename);
                intent.putExtra("selectepisodelink", mSiteLink.get(position));
                intent.putExtra("camefrom", "selectepisode");
                context.getApplicationContext().startActivity(intent);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Downloader(mSiteLink.get(position), context, activity, animename, String.valueOf(position + 1)).execute();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mEpisodeList.size();
    }

}

