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
    Button mRestartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death_activty);

        mScoreTextView = findViewById(R.id.tv_deathscreen_score);
        mRestartButton = findViewById(R.id.btn_restart);


        SharedPreferences sharedPreferences = getSharedPreferences("mySPRFILE", 0);
        int studienTage = sharedPreferences.getInt("studientage", 0);
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
