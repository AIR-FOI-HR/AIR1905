package com.example.herbertgame;

import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import hr.foi.air.herbert.engine.common.events.OnGameControllerListener;
import hr.foi.air.herbert.engine.common.interfaces.PlayHerbert;
import hr.foi.air.herbert.engine.logic.terrain.Terrain;
import hr.foi.air.herbert.engine.logic.terrain.TerrainList;
import hr.foi.air.herbert.engine.logic.terrain.TerrainLogic;
import hr.foi.air.herbert.engine.logic.terrain.TerrainMark;


public class GameView extends SurfaceView implements Runnable {

    private SurfaceHolder holder;
    volatile boolean playing;
    Thread gameThread = null;
    TerrainList terrainList = TerrainList.getInstance();
    TerrainLogic terrainLogic = TerrainLogic.getInstance(new OnGameControllerListener() {
        @Override
        public void OnLevelSolved(int levelScore) {

        }

        @Override
        public void OnCodeWithError(String error) {

        }
    });

    final Paint wallPaint = new Paint();
    final Paint foodPaint = new Paint();
    final Paint herbertPaint = new Paint();
    final Paint herbertBackPaint = new Paint();
    final Paint poisonPaint = new Paint();

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        herbertPaint.setColor(Color.BLUE);
        herbertBackPaint.setColor(Color.WHITE);
        foodPaint.setColor(Color.GREEN);
        wallPaint.setColor(Color.BLACK);
        poisonPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        //Metoda za crtanje levela.
        if(playing) {
            terrainList.playHerbertStepByStep(new PlayHerbert() {
                @Override
                public void playHerbertStep(Terrain terrain) {
                    Canvas canvas = holder.lockCanvas();
                    canvas.drawRGB(200, 200, 200);

                    int terrainSize = terrain.getSize();

                    int xSpacing = canvas.getWidth() / terrainSize;
                    int ySpacing = canvas.getHeight() / terrainSize;

                    for (int i = 0; i < terrainSize; i++) {
                        for (int j = 0; j < terrainSize; j++) {
                            float xStart = i * xSpacing;
                            float yStart = j * ySpacing;

                            //Ako se ne radi o bloku gdje je Herbert
                            if (terrain.getMark(j, i).getMark() < 256) {
                                switch (terrain.getMark(j, i).getMark()) {
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
                                switch (terrain.getMark(j, i).getMark() - 256) {
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
            }, null);
            pause();
        }
    }

    public void playSteps(String herbertCode){
        if(herbertCode != "") {
            terrainLogic.parseUserCode(herbertCode);
        }
        //Ako je herbertCode prazan string, tada se prikazuje samo inicijalno stanje terena.
        playing = true;
    }

    public void setLevelName(String levelName){
        terrainLogic.setLevelName(levelName);
        terrainLogic.setTerrainSize(15);
        Terrain initialTerrain = null;
        //Čitanje levela iz datoteke
        try {
            initialTerrain = terrainLogic.loadCurrentLevelTerrain(this.getContext());
        }catch (IOException e){

        }
        terrainList.add(initialTerrain);
    }

    public void pause(){
        playing = false;
        try{
            gameThread.join();
            return;
        } catch (InterruptedException e){

        }
    }

    public void resume(String code){
        playSteps(code);
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
