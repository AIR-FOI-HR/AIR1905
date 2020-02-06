package com.example.herbertgame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


import hr.foi.air.herbert.engine.common.events.OnGameControllerListener;
import hr.foi.air.herbert.engine.common.interfaces.PlayHerbert;
import hr.foi.air.herbert.engine.logic.terrain.Terrain;
import hr.foi.air.herbert.engine.logic.terrain.TerrainList;
import hr.foi.air.herbert.engine.logic.terrain.TerrainLogic;
import hr.foi.air.herbert.engine.logic.terrain.TerrainMark;


public class GameView extends SurfaceView implements Runnable {
    private Bitmap posjeceno;
    private Bitmap prazno;
    private Bitmap hrana;
    private Bitmap zid;
    private Bitmap otrov;
    private Bitmap right;
    private Bitmap left;
    private Bitmap up;
    private Bitmap down;
    private Bitmap herbert;

    private String levelName;

    private Rect blokovi[][];

    private SurfaceHolder holder;
    Canvas canvas;
    TerrainMark terrainMarks[][];
    volatile boolean playing;
    volatile boolean running;
    Thread gameThread = null;
    TerrainList terrainList = TerrainList.getInstance();
    TerrainLogic terrainLogic = TerrainLogic.getInstance(new OnGameControllerListener() {
        @Override
        public void OnLevelSolved(final int levelScore) {
            final String level = levelName;
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.level_complete_dialog);
                    dialog.setCancelable(true);

                    Button dialogOK = dialog.findViewById(R.id.button_ok);
                    TextView scoreText = dialog.findViewById(R.id.final_score);
                    scoreText.setText("Level completed!\nFinal score: " + levelScore);
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if(!((Activity) getContext()).isFinishing())
                        dialog.show();
                    else{
                        Toast.makeText(getContext(), "Nešto je pošlo po zlu!", Toast.LENGTH_SHORT);
                        ((Activity) getContext()).finish();
                    }

                    Context context = getContext();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("personal-bests", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(!sharedPreferences.contains(level)){
                        editor.putInt(level, levelScore);
                        Log.d("DODAVANJE", "run: " + level);
                        editor.commit();
                    }
                    else if(levelScore > sharedPreferences.getInt(level, 0)){
                        editor.putInt(level, levelScore);
                        Log.d("DODAVANJE", "run: " + level);
                        editor.commit();
                    }
                }
            });
        }

        @Override
        public void OnCodeWithError(String error) {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        }
    });


    OnGameEventListener callback;

    public void setOnGameEventListener(OnGameEventListener callback){
        this.callback = callback;
    }

    public interface OnGameEventListener{
        public void onScoreChange(int score);
    }


    public GameView(Context context) {
        super(context);
        blokovi = new Rect[15][15];
        holder = getHolder();
        SetupBitmaps();
    }

    private void SetupBitmaps(){
        posjeceno = BitmapFactory.decodeResource(getResources(), R.drawable.tile_posjeceno);
        prazno = BitmapFactory.decodeResource(getResources(), R.drawable.tile_prazno);
        hrana = BitmapFactory.decodeResource(getResources(), R.drawable.tile_hrana);
        zid = BitmapFactory.decodeResource(getResources(), R.drawable.tile_zid);
        otrov = BitmapFactory.decodeResource(getResources(), R.drawable.tile_otrov);
        right = BitmapFactory.decodeResource(getResources(),R.drawable.tile_path_right);
        left = BitmapFactory.decodeResource(getResources(),R.drawable.tile_path_left);
        up = BitmapFactory.decodeResource(getResources(),R.drawable.tile_path_up);
        down = BitmapFactory.decodeResource(getResources(),R.drawable.tile_path_down);
        herbert = BitmapFactory.decodeResource(getResources(), R.drawable.tile_herbert);
    }


    @Override
    public void run() {
        //Metoda za crtanje levela.
        //Izvršava se konstantno
        while(running) {
            //Izvršava se samo ako se desila promijena u unesenom kodu
            while (playing) {
                if (holder.getSurface().isValid()) {
                    terrainList.playHerbertStepByStep(new PlayHerbert() {
                        @Override
                        public void playHerbertStep(Terrain terrain) {
                            draw(terrain);
                            callback.onScoreChange(terrain.getScore());
                            terrainLogic.checkLevelSolved(terrain);
                        }
                    }, null);
                    playing = false;
                }
            }
        }
    }

    private void calculateCanvasDimensions(Canvas canvas) {
        // Visina i širina grida
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // prava visina i širina jednog pravokutnika
        int realwidth = (int) (width / 15);
        int realheight = (int) (height / 15);

        // razlika visine i širine nešto ( Početak grida top )
        int heightTopStart = 0;
        int left = 0;

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Rect r = new Rect();
                r.set(left + (j * realwidth), heightTopStart + (i * realheight), left + ((j + 1) * realwidth), heightTopStart + ((i + 1) * realheight));
                blokovi[i][j] = r;
            }
        }
    }

    private void draw(Terrain terrain){
        canvas = holder.lockCanvas();
        terrainMarks = terrain.getTerrainMarks();
        canvas.drawRGB(150, 150, 150);

        int terrainSize = terrain.getSize();

        calculateCanvasDimensions(canvas);

        for (int j = 0; j < terrainSize; j++) {
            for (int i = 0; i < terrainSize; i++) {
                if((terrainMarks[i][j].getMark() & TerrainMark.Prazno) == TerrainMark.Prazno){
                    canvas.drawBitmap(prazno, null, blokovi[i][j], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Hrana) == TerrainMark.Hrana){
                    canvas.drawBitmap(prazno, null, blokovi[i][j], null);
                    canvas.drawBitmap(hrana, null, blokovi[i][j], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Zid) == TerrainMark.Zid){
                    canvas.drawBitmap(zid, null, blokovi[i][j], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Otrov) == TerrainMark.Otrov){
                    canvas.drawBitmap(prazno, null, blokovi[i][j], null);
                    canvas.drawBitmap(otrov, null, blokovi[i][j], null);
                }
                if(i>0 && ((terrainMarks[i-1][j].getMark() & TerrainMark.Up) == TerrainMark.Up)){
                    canvas.drawBitmap(down, null, blokovi[i-1][j], null);
                    canvas.drawBitmap(up, null, blokovi[i][j], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Down) == TerrainMark.Down){
                    canvas.drawBitmap(up, null, blokovi[i][j], null);
                    canvas.drawBitmap(down, null, blokovi[i-1][j], null);
                }
                if(j>0 && ((terrainMarks[i][j-1].getMark() & TerrainMark.Left) == TerrainMark.Left)){
                    canvas.drawBitmap(right, null, blokovi[i][j-1], null);
                    canvas.drawBitmap(left, null, blokovi[i][j], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Right) == TerrainMark.Right){
                    canvas.drawBitmap(left, null, blokovi[i][j], null);
                    canvas.drawBitmap(right, null, blokovi[i][j-1], null);
                }
                if((terrainMarks[i][j].getMark() & TerrainMark.Herbert) == TerrainMark.Herbert){
                    canvas.drawBitmap(prazno, null, blokovi[i][j], null);
                    canvas.drawBitmap(herbert, null, blokovi[i][j], null);
                }
            }
        }
        holder.unlockCanvasAndPost(canvas);
    }

    public void playSteps(String herbertCode){
        //Ako je herbertCode prazan string, tada se prikazuje samo inicijalno stanje terena.
        Log.i("herbertCode", "playSteps: " + herbertCode);
        if (herbertCode != "") {
            terrainLogic.parseUserCode(herbertCode);
        }
        running = true;
        playing = true;
    }


    public void setLevelName(String levelName){
        this.levelName = levelName;
        terrainLogic.setLevelName(levelName);
        terrainLogic.setTerrainSize(15);
        Terrain initialTerrain = null;
        //Čitanje levela iz datoteke
        try {
            initialTerrain = terrainLogic.loadCurrentLevelTerrain(getContext());
        }catch (IOException e){

        }
        terrainList.add(initialTerrain);
    }

    public void pause(){
        running = false;
        playing = false;

        terrainList.clear();

        try{
            gameThread.join();
            return;
        } catch (InterruptedException e){

        }
    }

    public void resume(String code){
        playSteps(code);
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
