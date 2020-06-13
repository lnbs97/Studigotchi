package com.example.studigotchi;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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
import android.animation.ObjectAnimator;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private ImageView mStudiImageView;
    private ImageButton mLearnButton;
    private Boolean musicIsPlaying;
    private static final String TAG = "MainActivity";
    private Button musicButton;
    private Button infoButton;
    private AnimationDrawable animation_happy;
    private ProgressBar pbHorizontal;
    private TextView pbText;
    int currentProgress;
    long clickTime;
    boolean stillLearning = false;
    private Long quittime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer mpLearnSound = MediaPlayer.create(this, R.raw.learnsound);

        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //notificationChannel aufrufen
        createNotificationChannel();


        mStudiImageView = findViewById(R.id.imageView_studi);
        mLearnButton = findViewById(R.id.button_learn);
        musicButton = findViewById(R.id.button_music);
        infoButton = findViewById(R.id.button_info);
        pbHorizontal = findViewById(R.id.pbHorizontal);
        pbText = findViewById(R.id.pbText);


        mStudiImageView.setBackgroundResource(R.drawable.studianimation);
        animation_happy = (AnimationDrawable) mStudiImageView.getBackground();

        //wenn user auf info-button klickt
        // info activity oeffnen
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoActivity();
            }
        });

        // wenn User auf den Lautsprecher-Button klickt
        // Musik an bzw- ausschalten
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playmusic();
            }
        });

        // wenn User auf den Lernen-Button klickt
        // Bilder aendern und Sound starten
        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();
                //TODO richtigen Sound einfuegen
                mpLearnSound.start();
            }
        });
    }


    protected void startProg() {

        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();

        currentProgress = mySPR.getInt("currentProgress", 100);
        quittime = mySPR.getLong("quittime", 0);
        stillLearning = mySPR.getBoolean("stillLearning", false);

        long learnClickTime = mySPR.getLong("learnClickTime", 0);
        long currTime = System.currentTimeMillis() / 1000L;

        /*Studigotchi ist am lernen, Background Image wird auf Lernen gesetzt*/
        if (stillLearning == true && currTime <= learnClickTime) {
            mStudiImageView.setBackgroundResource(R.drawable.animation_learn);
        }
        //Wenn der Studi fertig gerlernt hat -> +30 Punkte und stillLearning auf false
        if (stillLearning == true && currTime >= learnClickTime) {
            currentProgress += 30;
            stillLearning = false;
        }
        //Wenn der Studi nicht mehr lernt, und nach "nach lernen Zeit" vorbei ist, Punktabzug
        //Außerdem wird das Bild durch checkState geprüft und ggf. abgeändert
        if (stillLearning == false && currTime > (learnClickTime + 10L)) {
            long passedTime = System.currentTimeMillis();
            passedTime = (passedTime - quittime) / 1000;
            //Todo: Sekunden abändern zu Stunden
            currentProgress -= passedTime * 0.83;
            mStudiImageView.setBackgroundResource(R.drawable.studianimation);


        }
        updateText();
        editor.putInt("currentProgress", currentProgress).commit();

        if (!stillLearning) {
            checkState(currentProgress);
        }

        editor.putBoolean("stillLearning", stillLearning).commit();

        ObjectAnimator.ofInt(pbHorizontal, "progress", currentProgress)
                .setDuration(2000)
                .start();

    }

    protected void updateText() {
        if (currentProgress > 100) {
            currentProgress = 100;
        }
        pbText.setText(currentProgress + "/" + pbHorizontal.getMax());
    }

    /*
    private void clearSharedPreferences() {
        this.getSharedPreferences("mySPRFILE", 0).edit().clear().commit();
    }

     */

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        playBackgroundSound();


        //Shared Prefs Datei öffnen
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();

        currentProgress = mySPR.getInt("currentProgress", 100);
        stillLearning = mySPR.getBoolean("stillLearning", false);
        pbText.setText(currentProgress + "/" + pbHorizontal.getMax());

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

        if (!firstRun)
            startProg();

    }

    public void playBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        startService(intent);
        musicIsPlaying = true;
        musicButton.setBackgroundResource(R.drawable.speaker_on);
    }


    /**
     * Aktualisiert die App und den Studi basierend auf seinem Leben
     * Wird bei jedem Öffnen ausgeführt
     *
     * @param health Leben, dass der Studi beim öffnen hat
     */
    private void checkState(int health) {
        if (health > 80) {
            //Setze glückliches Bild, sound, ...
            //TODO prüfen ob Studi gerade lernt, nur wenn er nicht lernt wird Bild geändert
            animation_happy.start();
            //mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_happy));
        } else if (health >= 50) {
            //Setze normales Bild und sounds
            //TODO Animation einfuegen?
            mStudiImageView.setBackgroundResource(R.drawable.studi_idle);
        } else if (health > 10) {
            // Setze unglückliches bild und sound
            //TODO Animation einfuegen?
            mStudiImageView.setBackgroundResource(R.drawable.studi_sad);
        } else if (health > 0) {
            //extrem kritischer Zustand
            //TODO Animation einfuegen?
            mStudiImageView.setBackgroundResource(R.drawable.studi_emergency);
        } else {
            // Studi stirbt
            // Deathscreen
            // Neustart
            //TODO Sound einfuegen?
            SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
            SharedPreferences.Editor editor = mySPR.edit();
            editor.putBoolean("stillLearning", false).commit();
            editor.putInt("studientage", 0).commit();
            currentProgress = 100;
            editor.putInt("currentProgress", currentProgress).commit();
            editor.putBoolean("firstRun", true).commit();
            Context context = MainActivity.this;
            Class destinationActivity = DeathActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);

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
        mStudiImageView.setBackgroundResource(R.drawable.animation_learn);
        animation_happy = (AnimationDrawable) mStudiImageView.getBackground();
        animation_happy.start();
        startAlarm();

        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();
        clickTime = System.currentTimeMillis() / 1000L;
        long learnClickTime = clickTime + 10L;
        editor.putLong("learnClickTime", learnClickTime).commit();
        stillLearning = true;
        editor.putBoolean("stillLearning", stillLearning).commit();
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
    private void openInfoActivity() {
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

        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();

        editor.putInt("currentProgress", currentProgress).commit();

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