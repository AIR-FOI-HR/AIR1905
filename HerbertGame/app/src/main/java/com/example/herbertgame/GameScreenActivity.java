package com.example.herbertgame;


import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;

import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.herbertgame.fragments.GameDisplayFragment;
import com.google.android.material.navigation.NavigationView;

public class GameScreenActivity extends AppCompatActivity implements GameDisplayFragment.OnCurrentScoreChangeListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Button startButton;
    private EditText codeInput;
    private GameDisplayFragment gameDisplayFragment;
    private TextView currentScore;
    private int score;
    private Switch switch_keyboard;
    MenuItem keyboard;
    private boolean isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle extras = getIntent().getExtras();

        String levelName = extras.getString("levelName");

        setContentView(R.layout.game_screen_activity);

        createGameViewFragment(levelName);

        codeInput = findViewById(R.id.code_input);
        currentScore = findViewById(R.id.current_score);



        keyboard = findViewById(R.id.sidebar_keyboard);
        switch_keyboard = findViewById(R.id.switch_keyboard);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //akcija nakon klika na gumb, uzima se tekst i salje dalje
                String herbertCode = codeInput.getText().toString();
                gameDisplayFragment.playSteps(herbertCode);
            }
        });
    }


    private void createGameViewFragment(String levelName) {
        Bundle bundle = new Bundle();
        bundle.putString("levelName", levelName);
        gameDisplayFragment = new GameDisplayFragment();
        gameDisplayFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.gameView, gameDisplayFragment);
        ft.commit();

        setContentView(R.layout.game_screen_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if(fragment instanceof GameDisplayFragment){
            GameDisplayFragment gameDisplayFragment = (GameDisplayFragment) fragment;
            gameDisplayFragment.setCurrentScoreChangeListener(this);
        }
    }

    @Override
    public void onCurrentScoreChange(int score) {
        this.score = score;
        currentScore.setText("Current score: " + score);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sidebar_keyboard:

                menuItem.setTitle("bla");
                break;
        }
        return false;
    }

    public void setOnClickListener(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        disableKeyboard();
    }
    private void disableKeyboard(){
        codeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
    }
}
