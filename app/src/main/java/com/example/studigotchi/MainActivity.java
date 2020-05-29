package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView mStudiImageView;
    private Button mLearnButton;
    private static final String TAG = "MainActivity";
    private Long timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStudiImageView = findViewById(R.id.imageView_studi);
        mLearnButton = findViewById(R.id.button_learn);

        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();
            }
        });

        Log.i(TAG, "onCreate");
    }

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        //SharedPref auslesen und timestamp schreiben
        timestamp = mySPR.getLong("myDate",0);
        System.out.println(timestamp);

        //timestamp ausgeben (Nur als Nachweis, dass es funktioniert)
        TextView textView = findViewById(R.id.textView_date);
        textView.setText("" + timestamp);
    }

    /**
     * Wird aufgerufen, wenn der Lernen Button gedrückt wird.
     * Hier bitte ergänzen, was passieren soll, wenn der Studi lernt.
     */
    private void learn() {
        // Bild ändern auf Lernen Bild
        mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_learning));
    }

    //Beim Pausieren/Schließen der App wird ein aktueller timestamp in den SharedPrefs gespeichert.
    protected void onPause(){
        super.onPause();

        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);

        //Editor Klasse initialisieren
        SharedPreferences.Editor sprEditor = mySPR.edit();

        long unixTime = System.currentTimeMillis();

        //CurrentDate in mySPR speichern
        sprEditor.putLong("myDate", unixTime);

        //Speichern
        sprEditor.commit();

    }
}
