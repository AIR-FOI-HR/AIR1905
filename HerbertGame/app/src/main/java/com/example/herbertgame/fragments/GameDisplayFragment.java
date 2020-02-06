package com.example.herbertgame.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.herbertgame.GameView;
import com.example.herbertgame.R;

public class GameDisplayFragment extends Fragment implements GameView.OnGameEventListener{

    public GameView gameView = null;


    @Override
    public void onScoreChange(int score) {
        callback.onLevelStateChange(score);
    }

    OnLevelStateChangeListener callback;

    public void setOnLevelStateChangeListener(OnLevelStateChangeListener callback){
        this.callback = callback;
    }

    public interface OnLevelStateChangeListener{
        public void onLevelStateChange(int currentScore);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameView = new GameView(container.getContext());
        String levelName = getArguments().getString("levelName");
        gameView.setLevelName(levelName);
        Log.i("dialog", "onCreateView: fragment");
        return gameView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameView.setOnGameEventListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onResume() {
        gameView.resume("");
        super.onResume();
    }

    public void playSteps(String code){
        gameView.playSteps(code);
    }
}
