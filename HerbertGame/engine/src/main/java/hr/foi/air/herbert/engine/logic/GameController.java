package hr.foi.air.herbert.engine.logic;

import android.util.Log;

import hr.foi.air.herbert.engine.common.events.OnGameControllerListener;
import hr.foi.air.herbert.engine.logic.terrain.Terrain;
import hr.foi.air.herbert.engine.logic.terrain.TerrainMark;

/**
 * Created by Filka Milip on 23.10.17..
 *
 * This class is used for controlling game status: if there is no more food left on terrain,
 * game is completed. If the poison is eaten, the game will end.
 *
 * */

public class GameController {
    private int foodLeft;
    private int startingFood;
    OnGameControllerListener listner;
    int score;

    /*
    Time counting:
     */
    long timeStart;
    long timeEnd;


    public GameController(OnGameControllerListener listener){
        /*
         * This function is temporary here to test PopUp. Function countFood()
         * and initiatePopUpWindow() should not be here in future
         */
        this.listner = listener;
        startGameTimer();
    }

    public void countFood(Terrain terrain, Boolean initial){
        /*
         * int total is number of total food used for counting
         */
        int total = 0;
        /*
         * Fill with algorithm for counting food
         * */

        TerrainMark terrainMarks[][] = terrain.getTerrainMarks();
        int terrainSize = terrain.getSize();


        for(int i = 0; i < terrainSize; i++){
            for(int j = 0; j < terrainSize; j++){
                if((TerrainMark.Hrana & terrainMarks[j][i].getMark()) == TerrainMark.Hrana)
                    total++;
            }
        }
        setFoodLeft(total);
        if(initial) startingFood = total;

        if(total == 0){
            listner.OnLevelSolved(0);
        }
    }
    public void resetFoodLeft(){
        setFoodLeft(startingFood);
    }

    private void startGameTimer(){
        timeStart= System.currentTimeMillis();
    }

    private void endGameTimer(){
        timeEnd = System.currentTimeMillis();
    }

    private long timeElapsed(){
        return timeEnd-timeStart;
    }

    /**
     * This function is called after counting food when reading map
     *
     * @param foodLeft stores amount of food left
     */
    public void setFoodLeft(int foodLeft) {
        this.foodLeft = foodLeft;
    }

    public void foodLeftdec(){
        foodLeft--;
        Log.i("food-count", "foodLeftdec: " + foodLeft);
        if (foodLeft==0){
            endGameTimer();
            //TODO - Deal with score.
        }
    }
}
