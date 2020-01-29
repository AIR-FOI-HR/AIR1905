package com.example.herbertgame;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;

import com.example.herbertgame.fragments.GameDisplayFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.textfield.TextInputLayout;

public class GameScreenActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

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
    }

    private void createGameViewFragment(String levelName){
        Bundle bundle = new Bundle();
        bundle.putString("levelName", levelName);
        GameDisplayFragment gameDisplayFragment = new GameDisplayFragment();
        gameDisplayFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.gameView, gameDisplayFragment);
        ft.commit();

        setContentView(R.layout.game_screen_activity);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();


    }
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
}
