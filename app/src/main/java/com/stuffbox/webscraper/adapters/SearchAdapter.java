package com.stuffbox.webscraper.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.activities.selectEpisode;
import com.stuffbox.webscraper.models.Anime;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private ArrayList<Anime> mAnimeList = new ArrayList<>();


    private Context context;
    private Activity activity;
   public  SearchAdapter(Context context, ArrayList<Anime> AnimeList,Activity activity) {
        this.mAnimeList = AnimeList;
        this.context=context;
        this.activity=activity;
    }
    public    SearchAdapter()
    {

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        private TextView title, episodeno;
        private ImageView imageofanime;

        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.animename);
            episodeno =  view.findViewById(R.id.episodeno);
            imageofanime= view.findViewById(R.id.img);
            cardView= view.findViewById(R.id.cardview);
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.title.setText(mAnimeList.get(position).getName());
        holder.episodeno.setText(mAnimeList.get(position).getEpisodeNo());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, selectEpisode.class);
                intent.putExtra("link",mAnimeList.get(position).getLink());
               intent.putExtra("animename",mAnimeList.get(position).getName());
               intent.putExtra("imageurl",mAnimeList.get(position).getImageLink());
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.getApplicationContext().startActivity(intent);
             activity.overridePendingTransition(R.anim.anime_slide_in_top,R.anim.anime_slide_out_top);
            }
        });
        Picasso.get().load(mAnimeList.get(position).getImageLink()).into(holder.imageofanime);
    }

    @Override
    public int getItemCount() {
        return mAnimeList.size();
    }

}

