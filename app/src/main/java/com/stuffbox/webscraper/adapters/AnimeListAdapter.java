package com.stuffbox.webscraper.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stuffbox.webscraper.activities.AnimeList;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.models.Anime;
import com.stuffbox.webscraper.activities.selectEpisode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.MyViewHolder> {

    private ArrayList<Anime> mAnimeList = new ArrayList<>();
    private Context context;
    Activity activity;
  public   AnimeListAdapter(Context context, ArrayList<Anime> AnimeList ,Activity activity) {
        this.mAnimeList = AnimeList;
        this.context=context;
        this.activity=activity;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;

        private TextView title;
       private  ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.animen);
            layout=view.findViewById(R.id.layout);
            imageView=view.findViewById(R.id.animeimage);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterforanimelist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(mAnimeList.get(position).getName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, selectEpisode.class);
                intent.putExtra("link",mAnimeList.get(position).getLink());
                intent.putExtra("animename",mAnimeList.get(position).getName());
              // intent.putExtra("imageurl","https://images.gogoanime.tv/cover/yuuyuuhakusho-specials.png");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                activity.overridePendingTransition(R.anim.anime_slide_in_top,R.anim.anime_slide_out_top);
            }
        });
        if(AnimeList.loadImages==1) {    // load images only when searching
             holder.setIsRecyclable(false);
            holder.imageView.setImageDrawable(null);
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mAnimeList.get(position).getImageLink()).into(holder.imageView);
        }
        else {
            holder.setIsRecyclable(true);
            holder.imageView.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return mAnimeList.size();
    }
  public   void setFilter(ArrayList<Anime> animeList)
    {
        mAnimeList=new ArrayList<>();
        mAnimeList.addAll(animeList);

        notifyDataSetChanged();
    }
}

