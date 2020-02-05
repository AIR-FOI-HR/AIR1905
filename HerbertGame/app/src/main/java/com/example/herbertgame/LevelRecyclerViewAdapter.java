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

public class LevelRecyclerViewAdapter extends RecyclerView.Adapter<LevelRecyclerViewAdapter.LevelViewHolder>{

    private ArrayList<String> levelNames;
    private ArrayList<String> worldRecords;
    //missing personalBests and levelImages
    private Context context;

    public LevelRecyclerViewAdapter(ArrayList<String> levelNames, Context context, ArrayList<String> worldRecords) {
        this.levelNames = levelNames;
        this.worldRecords = worldRecords;
        this.context = context;
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

        holder.levelName.setText("Level: " + levelNames.get(position));
        holder.worldRecord.setText("World record: " + worldRecords.get(position));
        //missing personalBests and levelImages

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
