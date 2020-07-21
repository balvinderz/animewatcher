package com.stuffbox.webscraper.adapters;


import android.content.Context;
import android.content.Intent;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.activities.WatchVideo;
import com.stuffbox.webscraper.database.AnimeDatabase;
import com.stuffbox.webscraper.models.Anime;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class AnimeDataAdapter extends RecyclerView.Adapter<AnimeDataAdapter.MyViewHolder> {

    private List<Anime> mAnimeList;

    int size;
    private Context context;

    public AnimeDataAdapter(Context context, List<Anime> AnimeList) {
        this.context = context;
        this.mAnimeList = AnimeList;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title, episodeno;
        private ImageView imageofanime;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.animename);
            episodeno = view.findViewById(R.id.episodeno);
            imageofanime = view.findViewById(R.id.img);
            cardView = view.findViewById(R.id.cardview);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(mAnimeList.get(position).getName());
        holder.episodeno.setText("Episode " + mAnimeList.get(position).getEpisodeNo());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WatchVideo.class);
                intent.putExtra("link", mAnimeList.get(position).getLink());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                int ep = holder.episodeno.getText().toString().lastIndexOf(" ");
                size = 0;
                AnimeDatabase database = AnimeDatabase.getInstance(context);

                Anime temp = mAnimeList.get(position);
                Anime databaseAnime = database.animeDao().getAnimeByNameAndEpisodeNo(temp.getName(), temp.getEpisodeNo());
                Log.i("yotimername", temp.getName());
                Log.i("yotimerepisode", String.valueOf(temp.getEpisodeNo()));

                String time = "0";
                if (databaseAnime != null) {
                    time = databaseAnime.getTime();
                }
                database.animeDao().deleteAnimeByNameAndEpisodeNo(temp.getName(), temp.getEpisodeNo());
                Anime anime = new Anime(temp.getName(), temp.getLink(), temp.getEpisodeNo(), temp.getImageLink(), time);
                database.animeDao().insertAnime(anime);
                intent.putExtra("animename", temp.getName());
                intent.putExtra("imagelink", temp.getImageLink());
                intent.putExtra("time", time);

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

