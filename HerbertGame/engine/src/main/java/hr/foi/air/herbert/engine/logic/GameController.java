package hr.foi.air.herbert.engine.logic;

import hr.foi.air.herbert.engine.common.events.OnGameControllerListener;
/**
 * Created by Filka Milip on 23.10.17..
 *
 * This class is used for controlling game status: if there is no more food left on terrain,
 * game is completed. If the poison is eaten, the game will end.
 *
 * */

public class GameController {
    private int foodLeft;
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
        countFood();
        this.listner = listener;
        startGameTimer();
    }

    public void countFood(){
        /*
         * int total is number of total food used for counting
         */
        int total=0;
        /*
         * Fill with algorithm for counting food
         * */
        setFoodLeft(total);
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

    void foodLeftdec(){
        foodLeft--;
        score += 50;
        if (foodLeft==0){
            endGameTimer();
            //TODO - Deal with score.
            listner.OnLevelSolved(0);
        }
    }
}
