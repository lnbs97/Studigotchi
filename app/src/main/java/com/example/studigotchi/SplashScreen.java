package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3500)
                .withBackgroundColor(Color.parseColor("#1a1b29"))
                .withHeaderText("\nStudigotchi")
                .withFooterText("Viel Spaß beim Spielen\n")
                .withBeforeLogoText("Studigotchi - Informatikprojekt 1\n\n")
                .withAfterLogoText("\n\nvon Julian Dohmen, Jonas Feige, Leo Nobis")
                //TODO Logo aendern
                .withLogo(R.mipmap.ic_launcher_round);
        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
