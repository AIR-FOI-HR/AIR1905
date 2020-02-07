package com.example.herbertgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.herbertgame.Login.GoogleSignOutActivity;
import com.example.herbertgame.Login.LoginActivity;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity {


    Button login;
    Button signOut;
    Button exit;
    Button levels;

    private boolean isSignedIn;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView robotLuck=(ImageView) findViewById(R.id.robotmessage);
        robotLuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Lucky tap :)", Toast.LENGTH_LONG).show();
            }
        });

        levels = findViewById(R.id.go_to_levels);
        levels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LevelListActivity.class);
                startActivity(intent);
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if ((accessToken != null && !accessToken.isExpired()) || account != null){
            isSignedIn = true;
        }
        else isSignedIn = false;

        login = findViewById(R.id.go_to_login);
        signOut = findViewById(R.id.go_to_signout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                if(accessToken != null && !accessToken.isExpired())  //ako je ulogiran na Facebook
                {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else //ako je ulogiran na Google
                {
                    Intent intent = new Intent(MainActivity.this, GoogleSignOutActivity.class);
                    startActivity(intent);
                }
            }
        });


        if(isSignedIn){
            login.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }
        else{
            signOut.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }

        exit = findViewById(R.id.exit_app);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if ((accessToken != null && !accessToken.isExpired()) || account != null){
            isSignedIn = true;
        }
        else isSignedIn = false;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                if(accessToken != null && !accessToken.isExpired())  //ako je ulogiran na Facebook
                {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else //ako je ulogiran na Google
                {
                    Intent intent = new Intent(MainActivity.this, GoogleSignOutActivity.class);
                    startActivity(intent);
                }
            }
        });


        if(isSignedIn){
            login.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }
        else{
            signOut.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }
}
