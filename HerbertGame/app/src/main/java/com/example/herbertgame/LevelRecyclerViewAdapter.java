package com.example.herbertgame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

public class LevelRecyclerViewAdapter extends RecyclerView.Adapter<LevelRecyclerViewAdapter.LevelViewHolder>{

    private List<ListItem> listItems;
    private ArrayList<String> levelNames;
    //missing worldRecords, personalBests; u onoj gore listi bude sve
    private Context context;


    //ovo se doda u kontruktor
    //List<ListItem> listItems
    //ArrayList<String> levelNames
    public LevelRecyclerViewAdapter(List<ListItem> listItems, Context context ,ArrayList<String> levelNames) {
        this.levelNames = levelNames;
        this.context = context;

        this.listItems=listItems;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_list_activity_item, parent, false);
        LevelViewHolder levelViewHolder = new LevelViewHolder(view);
        return levelViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, final int position) {

        // ListItem listItem=listItems.get(position);


        holder.levelName.setText("Level: " + levelNames.get(position));
        //missing worldRecord,personalBest and levelImage

        holder.levelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GameScreenActivity.class);
                intent.putExtra("levelName", levelNames.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelNames.size();
    }

    public class LevelViewHolder extends RecyclerView.ViewHolder{

        TextView levelName;
        TextView worldRecord;
        TextView personalBest;
        LinearLayout levelItem;
        ImageView levelImage;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelName = itemView.findViewById(R.id.level_name);
            worldRecord = itemView.findViewById(R.id.world_record);
            personalBest = itemView.findViewById(R.id.personal_best);
            levelImage = itemView.findViewById(R.id.level_image);
            levelItem = itemView.findViewById(R.id.level_item);
        }
    }
}
