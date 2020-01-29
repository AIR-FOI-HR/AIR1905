package com.example.herbertgame;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class LevelListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_list_activity);
        setTitle("LEVELS");

        String[] levelList = new String[0];

        //Gets level (asset) names into levelList
        AssetManager levels = this.getAssets();
        try {
            levelList = levels.list("maps");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Generates buttons equal to the number of levels
        for (int i = 1; i <= levelList.length; i++) {
            Button button = new Button(this);
            button.setText("Level " + i);
            button.setId(i);
            final int id_ = button.getId();

            LinearLayout button_layout = findViewById(R.id.level_button_layout);
            button_layout.addView(button);

            final String[] finalLevelList = levelList;
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String[] levelName = finalLevelList[finalI -1].split("\\."); //gets the name of the level into levelName[0]; cuts off the .txt extension

                    Intent intent = new Intent(LevelListActivity.this, GameScreenActivity.class);
                    intent.putExtra("levelName", levelName[0]);  //sends the name of the level to the GameScreenActivity,
                    startActivity(intent);
                }
            });

        }
    }
}
