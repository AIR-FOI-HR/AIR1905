package com.example.herbertgame;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herbertgame.JsonReader.JsonTask;
import com.example.herbertgame.JsonReader.OnTaskCompleted;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelListActivity extends AppCompatActivity implements OnTaskCompleted {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private static List<ListItem> listItems = new ArrayList<>();


    //variables
    //ako kopiram url(php), mora se promjenit http u https jer inače ne želi, ovaj radi
    private static final String URL_DATA= "https://cortex.foi.hr/air_herbert/getBestResultForEachLevel.php";

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


        JsonTask jsonTask=new JsonTask(this);
        jsonTask.delegate=this;
        jsonTask.execute(URL_DATA);

        /*
        Integer i=listItems.size();
        Toast.makeText(LevelListActivity.this, i.toString(), Toast.LENGTH_LONG).show();
*/

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


    @Override
    public void processFinish(String result) {

        //Toast.makeText(LevelListActivity.this, result, Toast.LENGTH_LONG).show();
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray=json.getJSONArray("levelList");

            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject o=jsonArray.getJSONObject(i);

                ListItem item=new ListItem(
                  o.getString("level"),
                  o.getString("best_result")
                );

                listItems.add(item);

            }

            //Integer i=listItems.size();
            //Toast.makeText(LevelListActivity.this, i.toString(), Toast.LENGTH_LONG).show();
            PrikazTosta(listItems);

        }
        catch (Throwable t)
        {
            Toast.makeText(LevelListActivity.this, "Greska", Toast.LENGTH_LONG).show();
        }

    }

    private void PrikazTosta(List<ListItem> listItems)
    {
        Integer i=listItems.size();
        Toast.makeText(LevelListActivity.this, i.toString(), Toast.LENGTH_LONG).show();

    }

}
