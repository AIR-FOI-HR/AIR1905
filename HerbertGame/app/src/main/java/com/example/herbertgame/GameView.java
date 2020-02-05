package com.example.herbertgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Rect;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

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
        public void OnLevelSolved(int levelScore) {
            Log.i("food-count", "OnLevelSolved");
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "LEVEL ZAVRŠEN", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void OnCodeWithError(String error) {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        }
    });

    OnScoreChangeListener callback;

    public void setOnScoreChangeListener(OnScoreChangeListener callback){
        this.callback = callback;
    }

    public interface OnScoreChangeListener{
        public void onScoreChange(int score);
    }

    public GameView(Context context) {
        super(context);
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

    private void draw(Terrain terrain){
        canvas = holder.lockCanvas();
        terrainMarks = terrain.getTerrainMarks();
        canvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));

        int terrainSize = terrain.getSize();

        int xSpacing = canvas.getWidth() / terrainSize;
        int ySpacing = canvas.getHeight() / terrainSize;

        for (int i = 0; i < terrainSize; i++) {
            for (int j = 0; j < terrainSize; j++) {
                float xStart = i * xSpacing;
                float yStart = j * ySpacing;

                //Ako se ne radi o bloku gdje je Herbert
                if (terrainMarks[j][i].getMark() < 256) {
                    switch (terrainMarks[j][i].getMark()) {
                        case TerrainMark.Prazno:
                            break;

                        case TerrainMark.Zid:
                            canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, new Paint());
                            break;

                        case TerrainMark.Hrana:
                            canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, foodPaint);
                            break;

                        case TerrainMark.Otrov:
                            canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, poisonPaint);
                            break;
                    }
                } else {
                    switch (terrainMarks[j][i].getMark() - 256) {
                        //Na bloku se nalazi Herbert, treba otkriti u kojoj je orijentaciji
                        case 16:
                            //Orijentacija UP
                            canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing / 2, herbertPaint);
                            canvas.drawRect(xStart, yStart + ySpacing / 2, xStart + xSpacing, yStart + ySpacing, herbertBackPaint);
                            break;
                        case 128:
                            //Orijentacija LEFT
                            canvas.drawRect(xStart, yStart, xStart + xSpacing / 2, yStart + ySpacing, herbertPaint);
                            canvas.drawRect(xStart + xSpacing / 2, yStart, xStart + xSpacing, yStart + ySpacing, herbertBackPaint);
                            break;
                        case 32:
                            //Orijentacija RIGHT
                            canvas.drawRect(xStart, yStart, xStart + xSpacing / 2, yStart + ySpacing, herbertBackPaint);
                            canvas.drawRect(xStart + xSpacing / 2, yStart, xStart + xSpacing, yStart + ySpacing, herbertPaint);
                            break;
                        case 64:
                            //Orijentacija DOWN
                            canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing / 2, herbertBackPaint);
                            canvas.drawRect(xStart, yStart + ySpacing / 2, xStart + xSpacing, yStart + ySpacing, herbertPaint);
                            break;
                    }
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
