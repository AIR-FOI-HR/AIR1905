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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelListActivity extends AppCompatActivity {

    private RequestQueue mQ;

    //variables
    private ArrayList<String> levelNames = new ArrayList<>();
    private ArrayList<String> worldRecords = new ArrayList<>();
    private List<Integer> imageIDs = new ArrayList<>();
    //missing personalBests

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_list_activity);

        mQ = Volley.newRequestQueue(this);
        final LevelListActivity activity = this;

        this.getLevelNames();
        this.getWorldRecords(new RecordsListener() {
            @Override
            public void OnRecordsReceived() {
                activity.initLevelRecyclerView();
            }
        });

    }



    private interface RecordsListener
    {
        void OnRecordsReceived();
    }


    private void getLevelNames(){
        String[] levelList = new String[0];
        AssetManager levels = this.getAssets();
        try {
            levelList = levels.list("maps");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String level:levelList
             ) {
            String[] levelNamesList = level.split("\\.");
            levelNames.add(levelNamesList[0]);
            int imageID = getResources().getIdentifier(levelNamesList[0], "drawable", getPackageName()); //gets ID of image in drawables with the same name as the level
            imageIDs.add(imageID);
        }
    }


    private void getWorldRecords(final RecordsListener listener){
        String url = "http://cortex.foi.hr/air_herbert/getBestResultForEachLevel.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("levelList");
                            for(int i = 0; i<jsonArray.length(); i++){
                                JSONObject level = jsonArray.getJSONObject(i);

                                worldRecords.add(level.getString("best_result"));
                                listener.OnRecordsReceived();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQ.add(request);
    }

    private void initLevelRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.level_recycler_view);
        LevelRecyclerViewAdapter adapter = new LevelRecyclerViewAdapter(levelNames, this, worldRecords, imageIDs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
