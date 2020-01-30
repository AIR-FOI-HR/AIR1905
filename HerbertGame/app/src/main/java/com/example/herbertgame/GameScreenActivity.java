package com.example.herbertgame;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.herbertgame.fragments.GameDisplayFragment;

public class GameScreenActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Button startButton;
    private EditText codeInput;

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

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //akcija nakon klika na gumb, uzima se tekst i salje dalje
            }
        });
    }

    private void createGameViewFragment(String levelName) {
        Bundle bundle = new Bundle();
        bundle.putString("levelName", levelName);
        GameDisplayFragment gameDisplayFragment = new GameDisplayFragment();
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
}
