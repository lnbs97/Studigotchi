package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeathActivity extends AppCompatActivity {

    TextView mScoreTextView;
    TextView mHighscoreTextView;
    Button mRestartButton;

    private int studyDays;

    /**
     * Verhindert, dass man zurückspringen kann
     */
    @Override
    public void onBackPressed() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death_activty);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            studyDays = bundle.getInt("studyDays");
        }

        mScoreTextView = findViewById(R.id.tv_deathscreen_score);
        mHighscoreTextView = findViewById(R.id.tv_highscore);
        mRestartButton = findViewById(R.id.btn_restart);


        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        int highscoreTage = sharedPreferences.getInt("highscoreDays", 0);
        String highscoreName = sharedPreferences.getString("highscoreName", null);

        mHighscoreTextView.setText("Dein bislang bester Studi war " + highscoreName + " mit " + highscoreTage + " Tagen.");
        mScoreTextView.setText(String.valueOf(studyDays));
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
