package com.stuffbox.webscraper.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.activities.DownloadedVideo;
import com.stuffbox.webscraper.activities.WatchVideo;
import com.stuffbox.webscraper.application.AnimeWatcherApplication;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.downloader.DownloadTracker;
import com.stuffbox.webscraper.downloader.MyDownloaderService;
import com.stuffbox.webscraper.downloader.Sample;
import com.stuffbox.webscraper.models.Anime;

import java.util.Collections;
import java.util.List;

public class DownloadedAnimeDataAdapter extends RecyclerView.Adapter<DownloadedAnimeDataAdapter.MyViewHolder> {

    private List<Anime> mAnimeList;

    int size;
    private Context context;
    private DownloadTracker downloadTracker;
    AnimeWatcherApplication application;
    FragmentManager fragmentManager;

    public DownloadedAnimeDataAdapter(Context context, List<Anime> AnimeList, AnimeWatcherApplication application, FragmentManager fragmentManager) {
        this.context = context;
        this.mAnimeList = AnimeList;
        this.application = application;
        this.fragmentManager = fragmentManager;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title, episodeno;
        private ImageView imageofanime;
        private Button tempButton;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.animename);
            episodeno = view.findViewById(R.id.episodeno);
            imageofanime = view.findViewById(R.id.img);
            cardView = view.findViewById(R.id.cardview);
            tempButton = view.findViewById(R.id.temp_downloader);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_data, parent, false);

        return new MyViewHolder(itemView);
    }

    private static boolean isNonNullAndChecked(@Nullable MenuItem menuItem) {
        // Temporary workaround for layouts that do not inflate the options menu.
        return menuItem != null && menuItem.isChecked();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(mAnimeList.get(position).getName());
        holder.episodeno.setText("Episode " + mAnimeList.get(position).getEpisodeNo());
        holder.tempButton.setVisibility(View.GONE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anime anime = mAnimeList.get(position);

                Intent intent = new Intent(context, DownloadedVideo.class);
                intent.putExtra("contentId", anime.getName() + ";" + anime.getLink() + ";" + anime.getEpisodeNo() + ";" + anime.getImageLink() + ";");
                intent.putExtra("animename", mAnimeList.get(position).getName());
                intent.putExtra("imagelink", mAnimeList.get(position).getImageLink());
                intent.putExtra("link", mAnimeList.get(position).getLink());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                int ep = holder.episodeno.getText().toString().lastIndexOf(" ");
                size = 0;
                context.getApplicationContext().startActivity(intent);

            }
        });

        Picasso.get().load(mAnimeList.get(position).getImageLink()).into(holder.imageofanime);
    }

    @Override
    public int getItemCount() {
        return mAnimeList.size();
    }

}
