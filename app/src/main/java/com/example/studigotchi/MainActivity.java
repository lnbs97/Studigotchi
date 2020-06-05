package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
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


        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        mStudiImageView = findViewById(R.id.imageView_studi);
        mLearnButton = findViewById(R.id.button_learn);

        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();

                Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();
                //zu Testzwecken gibt es eine Benachrichtigung nach 10 Sekunden
                long oneHourInMillies = 1000*10;

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick+oneHourInMillies, pendingIntent);

            }
        });
    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = "This is channel 1";
            NotificationChannel channelLearn = new NotificationChannel("notifyLearn", "LEARN", NotificationManager.IMPORTANCE_DEFAULT);
            channelLearn.setDescription(description);
            channelLearn.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelLearn);
        }
    }

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();
        //SharedPref auslesen und timestamp schreiben
        timestamp = mySPR.getLong("quittime",0);

        boolean firstRun = mySPR.getBoolean("firstRun", true);

        //Abfrage, ob App das erste mal aufgerufen wurde
        if (firstRun) {

            //firstRun Boolean auf false
            editor.putBoolean("firstRun", false).commit();
            editor.putInt("health", 100);
            //first RunActivity aufrufen, um Studinamen zu vergeben
            openFirstRunActivity();
        }

        //name aus sharedpref in TextView schreiben
        TextView textview = findViewById(R.id.studiName);
        textview.setText(mySPR.getString("name", "Dein Studigotchi"));


        //timestamp ausgeben (Nur als Nachweis, dass es funktioniert)
        TextView textView = findViewById(R.id.textView_date);
        textView.setText("" + timestamp);
        Log.d(TAG, "onResume");
    }

    /**
     * Wird aufgerufen, wenn der Lernen Button gedrückt wird.
     * Hier bitte ergänzen, was passieren soll, wenn der Studi lernt.
     */
    private void learn() {
        // Bild ändern auf Lernen Bild
        mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_learning));
    }

    public void openFirstRunActivity(){
        Intent intent = new Intent(this, firstRunActivity.class);
        startActivity(intent);
    }

    //Beim Pausieren/Schließen der App wird ein aktueller timestamp in den SharedPrefs gespeichert.
    protected void onPause(){
        super.onPause();

        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);

        //Editor Klasse initialisieren
        SharedPreferences.Editor editor = mySPR.edit();

        long quitTime = System.currentTimeMillis();

        //CurrentDate in mySPR speichern
        editor.putLong("quittime", quitTime).commit();

    }
}
