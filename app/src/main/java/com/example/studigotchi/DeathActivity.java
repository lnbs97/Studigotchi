package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeathActivity extends AppCompatActivity {

    TextView mScoreTextView;
    TextView mHighscoreTextView;
    Button mRestartButton;


    /**
     * Verhindert, dass man zurückspringen kann
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death_activty);

        mScoreTextView = findViewById(R.id.tv_deathscreen_score);
        mHighscoreTextView = findViewById(R.id.tv_highscore);
        mRestartButton = findViewById(R.id.btn_restart);


        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        int studienTage = sharedPreferences.getInt("studientage", 0);
        int highscoreTage = sharedPreferences.getInt("highscoreTage", 0);
        String name = sharedPreferences.getString("name", "");

        if (studienTage > highscoreTage) {
            highscoreTage = studienTage;
            sharedPreferences.edit().putInt("highscoreTage", highscoreTage).commit();
            sharedPreferences.edit().putString("highscoreName", name).commit();
        }

        String highscoreName = sharedPreferences.getString("highscoreName", name);


        long firstRunTime = System.currentTimeMillis();
        sharedPreferences.edit().putLong("firstRunTime", firstRunTime);

        mHighscoreTextView.setText("Dein bislang bester Studi war " + highscoreName + " mit " + highscoreTage + " Tagen.");
        mScoreTextView.setText(String.valueOf(studienTage));
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = DeathActivity.this;
                Class destinationActivity = MainActivity.class;
                Intent intent = new Intent(context, destinationActivity);
                startActivity(intent);
            }
        });
    }
}
