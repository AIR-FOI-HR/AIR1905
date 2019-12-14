package com.example.herbertgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements Runnable {

    private Bitmap bmp;
    private SurfaceHolder holder;
    volatile boolean playing;
    Thread gameThread = null;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        float xOffset = 1;
        float yOffset = 1;
        float yDirection = 5;
        float xDirection = 6;
        while(playing){
            if(!holder.getSurface().isValid())
                continue;
            else{
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(100, 200 ,100);
                canvas.drawBitmap(bmp, xOffset, yOffset, null);
                if(yOffset + bmp.getHeight() >= canvas.getHeight() || yOffset < 0){
                    yDirection *= -1;
                }
                if(xOffset + bmp.getWidth() >= canvas.getWidth() || xOffset < 0){
                    xDirection *= -1;
                }
                holder.unlockCanvasAndPost(canvas);
                yOffset += yDirection;
                xOffset += xDirection;
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
