package com.example.herbertgame;

public class ListItem {

    private String level;
    private String best_result;
    //private String WordRecord;

    public ListItem(String level, String best_result){
        this.level=level;
        this.best_result=best_result;

    }

    public String getLevel(){
        return level;
    }
    public String getBest_result(){
        return best_result;
    }

}