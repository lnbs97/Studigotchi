package com.example.studigotchi;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private ImageView mStudiImageView;
    private ImageButton mLearnButton;
    private Boolean musicIsPlaying = true;
    private static final String TAG = "MainActivity";
    private ProgressBar mProgressbar;
    private Button musicButton;
    private Button infoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final MediaPlayer mpLearnSound = MediaPlayer.create(this, R.raw.learnsound);


        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //notificationChannel aufrufen
        createNotificationChannel();

        mStudiImageView = findViewById(R.id.imageView_studi);
        mLearnButton = findViewById(R.id.button_learn);
        mProgressbar = findViewById(R.id.pb_learning);
        musicButton = findViewById(R.id.button_music);
        infoButton = findViewById(R.id.button_info);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoActivity();
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playmusic();
            }
        });

        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();
                mpLearnSound.start();
            }
        });
    }


    private void clearSharedPreferences() {
        this.getSharedPreferences("mySPRFILE", 0).edit().clear().commit();
    }

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        playBackgroundSound();
        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();

        boolean firstRun = mySPR.getBoolean("firstRun", true);
        //Abfrage, ob App das erste mal aufgerufen wurde
        if (firstRun) {
            //firstRun Boolean auf false
            editor.putBoolean("firstRun", false).commit();
            //first RunActivity aufrufen, um Studinamen zu vergeben
            openFirstRunActivity();
        }


        // Studientage anzeigen lassen
        TextView studientageTextView = findViewById(R.id.tv_studientage);
        int studientage = getStudienTage();
        studientageTextView.setText("Tag " + studientage);
        editor.putInt("studientage", studientage);

        //name aus sharedpref in TextView schreiben
        TextView textview = findViewById(R.id.tv_studi_name);
        textview.setText(mySPR.getString("name", "Dein Studigotchi"));

        //TODO Leben neu berechenen und abspeichern
        editor.putInt("health", 100).commit(); //Um Tod etc. zu testen
        int health = mySPR.getInt("health", 0);
        //Update Progressbar
        mProgressbar.setProgress(health);
        //Prüfe Zustand des Studigotchis
        checkState(health);
    }

    public void playBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        startService(intent);
    }


    /**
     * Aktualisiert die App und den Studi basierend auf seinem Leben
     * Wird bei jedem Öffnen ausgeführt
     *
     * @param health Leben, dass der Studi beim öffnen hat
     */
    private void checkState(int health) {
        if (health == 100) {
            //Setze glückliches Bild, sound, ...
            //TODO prüfen ob Studi gerade lernt, nur wenn er nicht lernt wird Bild geändert
            mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_happy));
        } else if (health > 50) {
            //Setze normales Bild und sounds
            mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_idle));
        } else if (health < 50) {
            // Setze unglückliches bild und sound
            mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_sad));
            if (health <= 10) {
                //extrem kritischer Zustand
            }
            if (health <= 0) {
                // Studi stirbt
                // Deathscreen
                // Neustart
                Context context = MainActivity.this;
                Class destinationActivity = DeathActivity.class;
                Intent intent = new Intent(context, destinationActivity);
                startActivity(intent);
            }
        }
    }

    private int getStudienTage() {
        SharedPreferences sharedPreferences = getSharedPreferences("mySPRFILE", 0);
        long firstRunTime = sharedPreferences.getLong("firstRunTime", 0);
        long currentTime = System.currentTimeMillis();
        long timeAlive = currentTime - firstRunTime;

        int studienTage = (int) (timeAlive / 1000 / 60 / 60 / 4);
        return studienTage;
    }

    /**
     * Wird aufgerufen, wenn der music-button gedrueckt wird.
     * Die Musik wird an bzw ausgeschaltet und
     * das icon des Buttons gewechselt dementsprechend
     */
    private void playmusic() {
        if (musicIsPlaying) {
            stopBackgroundSound();
            musicButton.setBackgroundResource(R.drawable.speaker_off);
            musicIsPlaying = false;
        } else {
            playBackgroundSound();
            musicButton.setBackgroundResource(R.drawable.speaker_on);
            musicIsPlaying = true;
        }
    }

    /**
     * Wird aufgerufen, wenn der Lernen Button gedrückt wird.
     * Hier bitte ergänzen, was passieren soll, wenn der Studi lernt.
     */
    private void learn() {
        // Bild ändern auf Lernen Bild
        mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_learning));
        startAlarm();
    }

    /**
     * Notificationchannel wird gestartet.
     * Über diesen Channel wird später an den User die Nachricht
     * gesendet, dass der Studigotchi wieder lernen kann
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = "This is channel 1";
            NotificationChannel channelLearn = new NotificationChannel("notifyLearn", "LEARN", NotificationManager.IMPORTANCE_DEFAULT);
            channelLearn.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelLearn);
        }
    }

    /**
     * Alarm wird gestartet, indem ein PendingIntent aufgerufen wird.
     * Dafür dient der Broadcast-Service, der zum Alarm-Zeitpunkt über den Notficationchannel
     * eine Nachricht verschickt
     */
    private void startAlarm() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        long timeAtButtonClick = System.currentTimeMillis();
        //zu Testzwecken gibt es eine Benachrichtigung nach 10 Sekunden
        long oneHourInMillies = 1000 * 10;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAtButtonClick + oneHourInMillies, pendingIntent);

    }

    /**
     * openInfoActivity oeffnet InfoActivity und ermoeglicht dem User, Infos ueber die App zu bekommen
     */
    private void openInfoActivity(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    /**
     * openFirstActivity soll geöffnet werden, damit User seinen Namen eintragen kann
     */
    private void openFirstRunActivity() {
        Intent intent = new Intent(this, firstRunActivity.class);
        startActivity(intent);
    }

    //Beim Pausieren/Schließen der App wird ein aktueller timestamp in den SharedPrefs gespeichert.
    protected void onPause() {
        super.onPause();
        setQuitTime();
    }

    /**
     * Musik stoppen, indem man den entsprechenden Service schließt
     */
    public void stopBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        stopService(intent);
        Toast.makeText(getApplicationContext(), "Stop Backgroundmusic", Toast.LENGTH_SHORT).show();
    }

    private void setQuitTime() {
        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        //Editor Klasse initialisieren
        SharedPreferences.Editor editor = mySPR.edit();
        long quitTime = System.currentTimeMillis();
        //CurrentDate in mySPR speichern
        editor.putLong("quittime", quitTime).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBackgroundSound();
    }
}