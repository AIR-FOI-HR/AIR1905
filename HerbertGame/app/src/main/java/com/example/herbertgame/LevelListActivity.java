package com.example.herbertgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;


    //variables
    //ako kopiram url(php), mora se promjenit http u https jer inače ne želi, ovaj radi
    private static final String URL_DATA= "https://api.myjson.com/bins/1a95vi";

    private ArrayList<String> levelNames = new ArrayList<>();
    //missing worldRecords, personalBests and levelImages

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_list_activity);

        this.getLevelNames();
        this.initLevelRecyclerView();

        recyclerView = (RecyclerView) findViewById(R.id.level_recycler_view);
        //svaki item ima fix size
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems= new ArrayList<>();

        loadRecyclerViewData();
    }



    private void loadRecyclerViewData(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Učitavam podatke...");
        progressDialog.show();


        JsonObjectRequest zahtjev = new JsonObjectRequest(Request.Method.GET, URL_DATA, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        try {

                            JSONArray jsonArray = response.getJSONArray("listaLevela");


                            for(int i=0; i<jsonArray.length(); i++) {

                                JSONObject o = jsonArray.getJSONObject(i);

                                ListItem item = new ListItem(
                                        o.getString("level"),

                                        o.getString("best_result")


                                );

                                listItems.add(item);
                                //spremljeno je u listu
                                Toast.makeText(LevelListActivity.this, "dodano u listu :)", Toast.LENGTH_LONG).show();
                            }


                            //tu je greška neznam koja
                            //adapter = new LevelRecyclerViewAdapter(listItems, getApplicationContext());
                            //Toast.makeText(LevelListActivity.this, "dodano u listu :)", Toast.LENGTH_LONG).show();
                            //recyclerView.setAdapter(adapter);
                            //Toast.makeText(LevelListActivity.this, "dodano u listu :)", Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                            e.printStackTrace();


                            Toast.makeText(LevelListActivity.this, "Greška sa JSON-om :)", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(LevelListActivity.this, "tu sam :)", Toast.LENGTH_LONG).show();

            }
        }
        );




        //tu je bilo
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(zahtjev);

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
        }
    }

    private void initLevelRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.level_recycler_view);
        LevelRecyclerViewAdapter adapter = new LevelRecyclerViewAdapter(listItems, this,levelNames );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}
