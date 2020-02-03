package com.example.herbertgame.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.herbertgame.GameView;

public class GameDisplayFragment extends Fragment implements GameView.OnScoreChangeListener{

    public GameView gameView = null;


    @Override
    public void onScoreChange(int score) {
        callback.onCurrentScoreChange(score);
    }

    OnCurrentScoreChangeListener callback;

    public void setCurrentScoreChangeListener(OnCurrentScoreChangeListener callback){
        this.callback = callback;
    }

    public interface OnCurrentScoreChangeListener{
        public void onCurrentScoreChange(int currentScore);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameView = new GameView(container.getContext());
        String levelName = getArguments().getString("levelName");
        gameView.setLevelName(levelName);
        return gameView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameView.setOnScoreChangeListener(this);

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
