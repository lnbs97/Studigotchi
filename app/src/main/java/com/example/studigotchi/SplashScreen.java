package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {
    private Class targetActivity;
    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Prüfen ob firstRun gilt und gegebenfalls firstRunActivity aufrufen
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //first RunActivity aufrufen, um Studinamen zu vergeben
            targetActivity = firstRunActivity.class;
        }else targetActivity = MainActivity.class;

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(targetActivity)
                .withSplashTimeOut(1000)
                .withBackgroundColor(Color.parseColor("#1a1b29"))
                .withHeaderText("\nStudigotchi")
                .withFooterText("Viel Spaß beim Spielen\n")
                .withBeforeLogoText("Studigotchi - Informatikprojekt 1\n\n")
                .withAfterLogoText("\n\nvon Julian Dohmen, Jonas Feige, Leo Nobis")
                .withLogo(R.drawable.ic_launcher_foreground);
        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
