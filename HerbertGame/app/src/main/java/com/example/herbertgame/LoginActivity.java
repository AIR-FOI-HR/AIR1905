package com.example.herbertgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.herbertgame.GoogleLogin.GoogleSignOutActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onStart() {
        super.onStart();

        //provjera da li je prethodno ulogiran
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //ako vraća null nije ulogiran, ako vraća GoogleSignInAccount object već je ulogiran
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_google);
        signInButton = findViewById(R.id.google_sign_in_button);
    }
