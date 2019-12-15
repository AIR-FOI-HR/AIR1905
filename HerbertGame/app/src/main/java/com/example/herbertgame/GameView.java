package com.example.herbertgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import hr.foi.air.herbert.engine.logic.terrain.Terrain;
import hr.foi.air.herbert.engine.logic.terrain.TerrainMark;


public class GameView extends SurfaceView implements Runnable {

    private SurfaceHolder holder;
    volatile boolean playing;
    Thread gameThread = null;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        Terrain currentTerrain = new Terrain(20);
        int terrainSize = currentTerrain.getSize();

        currentTerrain.setMark(10, 6, TerrainMark.Hrana);

        currentTerrain.setMark(16, 17, TerrainMark.Otrov);


        currentTerrain.setMark(3, 2, TerrainMark.Zid);
        currentTerrain.setMark(3, 3, TerrainMark.Zid);
        currentTerrain.setMark(3, 4, TerrainMark.Zid);
        currentTerrain.setMark(3, 5, TerrainMark.Zid);
        currentTerrain.setMark(4, 5, TerrainMark.Zid);
        currentTerrain.setMark(5, 5, TerrainMark.Zid);
        currentTerrain.setMark(12, 8, TerrainMark.Zid);
        currentTerrain.setMark(12, 9, TerrainMark.Zid);
        currentTerrain.setMark(12, 10, TerrainMark.Zid);
        currentTerrain.setMark(11, 10, TerrainMark.Zid);
        currentTerrain.setMark(11, 11, TerrainMark.Zid);
        currentTerrain.setMark(11, 12, TerrainMark.Zid);

        currentTerrain.setMark(9, 9, TerrainMark.Herbert);



        Paint wallPaint = new Paint();
        Paint foodPaint = new Paint();
        Paint herbertPaint = new Paint();
        Paint poisonPaint = new Paint();


        herbertPaint.setColor(Color.BLUE);
        foodPaint.setColor(Color.GREEN);
        wallPaint.setColor(Color.BLACK);
        poisonPaint.setColor(Color.RED);

        while(playing){
            if(!holder.getSurface().isValid())
                continue;
            else{
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(200, 200 ,200);

                int xSpacing = canvas.getWidth() / terrainSize;
                int ySpacing = canvas.getHeight() / terrainSize;

                for(int i = 0; i < currentTerrain.getSize(); i++){
                    for(int j = 0; j < currentTerrain.getSize(); j++){
                        float xStart = i * xSpacing;
                        float yStart = j * ySpacing;

                        switch (currentTerrain.getMark(i, j).getMark()){
                            case TerrainMark.Prazno :
                                break;

                            case TerrainMark.Zid :
                                canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, new Paint());
                                break;

                            case TerrainMark.Hrana:
                                canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, foodPaint);
                                break;

                            case TerrainMark.Herbert:
                                canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, herbertPaint);
                                break;

                            case TerrainMark.Otrov:
                                canvas.drawRect(xStart, yStart, xStart + xSpacing, yStart + ySpacing, poisonPaint);
                                break;
                        }
                    }
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void pause(){
        playing = false;
        try{
            gameThread.join();
            return;
        } catch (InterruptedException e){

        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
