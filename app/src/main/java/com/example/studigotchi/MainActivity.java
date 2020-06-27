package com.example.studigotchi;

import android.animation.TimeAnimator;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    private ImageButton mFeedButton;
    private ImageButton mSleepButton;
    private ImageButton mPartyButton;
    private Boolean musicIsPlaying;
    private static final String LOG_TAG = "MainActivity";
    private Button musicButton;
    private Button infoButton;
    private AnimationDrawable backgroundAnimation;
    private ProgressBar pbLearn;
    private ProgressBar pbEnergy;
    private TextView pbLeanText;

    private Thread updateUIThread;
    private boolean isAppInForegeround;
    private boolean isUIThreadRunning;

    /* SharedPreferences Variablen START */

    private long onPauseTime;
    private long firstRunTime;

    private long learnClickTime;
    private long learnEndTime;
    private long energyClickTime;
    private long sleepClickTime;
    private long partyClickTime;

    private boolean isLearning;
    private boolean isEating;
    private boolean isSleeping;
    private boolean isPartying;
    private boolean isFirstRun;

    private String playerName;

    private int learnValue;
    private int energyValue;
    private int studyDays;
    private int gameSpeed;



    /* SharedPreferences Variablen ENDE */

    private void getSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);

        onPauseTime = sharedPreferences.getLong("onPauseTime", 0);
        firstRunTime = sharedPreferences.getLong("firstRunTime", 0);

        learnClickTime = sharedPreferences.getLong("learnClickTime", System.currentTimeMillis());
        learnEndTime = sharedPreferences.getLong("learnEndTime", System.currentTimeMillis());
        sleepClickTime = sharedPreferences.getLong("sleepClickTime", System.currentTimeMillis());
        partyClickTime = sharedPreferences.getLong("partyClickTime", System.currentTimeMillis());
        energyClickTime = sharedPreferences.getLong("energyClickTime", System.currentTimeMillis());

        isLearning = sharedPreferences.getBoolean("isLearning", false);
        isEating = sharedPreferences.getBoolean("isEating", false);
        isSleeping = sharedPreferences.getBoolean("isSleeping", false);
        isPartying = sharedPreferences.getBoolean("isPartying", false);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        playerName = sharedPreferences.getString("playerName", "EMPTY");

        learnValue = sharedPreferences.getInt("learnValue", 100);
        energyValue = sharedPreferences.getInt("energyValue", 50);
        studyDays = sharedPreferences.getInt("studyDays", 0);

    }

    private void updateSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("onPauseTime", onPauseTime)
                .putLong("firstRunTime", firstRunTime)
                .putLong("learnClickTime", learnClickTime)
                .putLong("learnEndTime", learnEndTime)
                .putLong("sleepClickTime", sleepClickTime)
                .putLong("partyClickTime", partyClickTime)
                .putLong("energyClickTime", energyClickTime)
                .putBoolean("isLearning", isLearning)
                .putBoolean("isEating", isEating)
                .putBoolean("isSleeping", isSleeping)
                .putBoolean("isPartying", isPartying)
                .putBoolean("isFirstRun", isFirstRun)
                .putString("playerName", playerName)
                .putInt("learnValue", learnValue)
                .putInt("energyValue", energyValue)
                .putInt("studyDays", studyDays).apply();
    }

    private void resetSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        firstRunTime = System.currentTimeMillis();

        isFirstRun = true;
        learnValue = 100;
        energyValue = 50;
        learnClickTime = System.currentTimeMillis();
        energyClickTime = System.currentTimeMillis();
        editor.putLong("onPauseTime", 0)
                .putLong("firstRunTime", firstRunTime)
                .putLong("learnClickTime", learnClickTime)
                .putLong("energyClickTime", energyClickTime)
                .putBoolean("isLearning", false)
                .putBoolean("isEating", false)
                .putBoolean("isSleeping", false)
                .putBoolean("isPartying", false)
                .putBoolean("isFirstRun", isFirstRun)
                .putString("playerName", "EMPTY")
                .putInt("learnValue", learnValue)
                .putInt("energyValue", 100)
                .putInt("studyDays", 0)
                .putInt("gameSpeed", 2000).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppInForegeround = true;


        final MediaPlayer mpLearnSound = MediaPlayer.create(this, R.raw.learn_sound);
        final MediaPlayer mpButtonSound = MediaPlayer.create(this, R.raw.button_press);
        final MediaPlayer mpSleepSound = MediaPlayer.create(this, R.raw.sleep_sound);
        final MediaPlayer mpPartySound = MediaPlayer.create(this, R.raw.party_sound);
        final MediaPlayer mpFeedSound = MediaPlayer.create(this, R.raw.feed_sound);
        final MediaPlayer mpWakeUpSound = MediaPlayer.create(this, R.raw.wake_up_sound);
        final MediaPlayer mpYawningSound = MediaPlayer.create(this, R.raw.yawning_sound);

        //notificationChannel aufrufen
        createNotificationChannel();

        // Get action buttons
        mLearnButton = findViewById(R.id.button_learn);
        mFeedButton = findViewById(R.id.button_feed);
        mSleepButton = findViewById(R.id.button_sleep);
        mPartyButton = findViewById(R.id.button_party);

        // Get control buttons
        musicButton = findViewById(R.id.button_music);
        infoButton = findViewById(R.id.button_info);

        // Get progress bar
        pbLearn = findViewById(R.id.pbHorizontal);
        pbEnergy = findViewById(R.id.pbEnergy);
        pbLeanText = findViewById(R.id.pbText);

        // get studi image
        mStudiImageView = findViewById(R.id.imageView_studi);

        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        gameSpeed = sharedPreferences.getInt("gameSpeed", 2000);

        startUIThread();
        isUIThreadRunning = true;

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
                if (isLearning || isSleeping || isEating || isPartying) return;
                learn();
                mpButtonSound.start();
                mpLearnSound.start();
            }
        });
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLearning || isSleeping || isEating || isPartying) return;
                feed();
                mpButtonSound.start();
                mpFeedSound.start();
            }
        });
        mSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLearning || isEating || isPartying) return;
                sleep();
                if (isSleeping) {
                    mpButtonSound.start();
                    mpSleepSound.start();
                } else {
                    mpButtonSound.start();
                    mpWakeUpSound.start();
                    mpYawningSound.start();
                }
            }
        });
        mPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLearning || isSleeping || isEating || isPartying) return;
                party();
                mpButtonSound.start();
                mpPartySound.start();
            }
        });
    }

    private void startUIThread() {
        if (!isUIThreadRunning) {
            updateUIThread = new Thread() {
                @Override
                public void run() {
                    while (isAppInForegeround) {
                        try {
                            sleep(1000);
                            if(!isLearning && !isSleeping && !isEating && !isPartying) {
                                updateLearnValue();
                                updateEnergyValue();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(isLearning || isPartying)
                                        checkState();
                                    updateEnergyPb();
                                    updateLearnPb();
                                    updateImage();
                                }
                            });
                            Log.i(LOG_TAG, "learnValue: " + learnValue);
                            Log.i(LOG_TAG, "energyValue: " + energyValue);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            updateUIThread.start();
        }
    }

    private void checkLearnStatus(){
        long currentTime = System.currentTimeMillis();

        if (isLearning && currentTime <= learnEndTime) {
            setAnimationLearn();
            disableButtons();
        } else {
            //Wenn Studi zu ende gelernt hat
            isLearning = false;
            enableButtons();
        }
    }

    private void updateEnergyValue() {
        long currentTime = System.currentTimeMillis();
        int energyLost = (int)((currentTime - energyClickTime) / (gameSpeed));
        Log.i(LOG_TAG, "energyClickTime: " + energyClickTime);
        if(energyLost > 0){
            energyValue -= energyLost;
            energyClickTime = System.currentTimeMillis();
        }
    }

    private void updateLearnValue() {
        long currentTime = System.currentTimeMillis();
        if (!isLearning && currentTime > learnEndTime) {
            Log.i(LOG_TAG, "learnEndTime: " + learnEndTime);
            Log.i(LOG_TAG, "currentTime: " + currentTime);
            Log.i(LOG_TAG, "gameSpeed: " + gameSpeed);
            Log.i(LOG_TAG, "result: " + (currentTime - learnEndTime) / (gameSpeed));
            int learnLost = (int)((currentTime - learnEndTime) / (gameSpeed));
            if(learnLost > 0){
                learnValue -= learnLost;
                learnEndTime = System.currentTimeMillis();
            }
        }
    }

    private void setAnimationHappy() {
        mStudiImageView.setBackgroundResource(R.drawable.studianimation);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationLearn() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_learn);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        getSharedPrefs();

        isAppInForegeround = true;
        startUIThread();
        isUIThreadRunning = true;

        playBackgroundSound();

        //Abfrage, ob App das erste mal aufgerufen wurde
        if (isFirstRun) {
            //isFirstRun Boolean auf false
            isFirstRun = false;
            /*Werte zuruecksetzen, falls User lange in der DeathActivity bleibt
            Studigotchi wuerde dann sofort wieder sterben*/
            learnClickTime = System.currentTimeMillis();
            energyClickTime = System.currentTimeMillis();
            //first RunActivity aufrufen, um Studinamen zu vergeben
            openFirstRunActivity();
        }

        //set up StudiImage
        checkState();

        // Studientage anzeigen lassen
        TextView studientageTextView = findViewById(R.id.tv_studientage);
        studyDays = getStudienTage();
        studientageTextView.setText("Tag " + studyDays);

        //name aus sharedpref in TextView schreiben
        TextView textview = findViewById(R.id.tv_studi_name);
        textview.setText(playerName);

    }

    private void checkPartyStatus() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > energyClickTime) {
            isPartying = false;
            enableButtons();
        }
    }

    private void updateImage() {

        if (!isLearning && !isPartying && !isSleeping && !isEating) {
            if (learnValue > 80) {
                //Setze glückliches Bild, sound, ...
                //TODO prüfen ob Studi gerade lernt, nur wenn er nicht lernt wird Bild geändert
                setAnimationHappy();
                //mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_happy));
            } else if (learnValue >= 50) {
                //Setze normales Bild und sounds
                //TODO Animation einfuegen?
                mStudiImageView.setBackgroundResource(R.drawable.studi_idle);
            } else if (learnValue > 10) {
                // Setze unglückliches bild und sound
                //TODO Animation einfuegen?
                mStudiImageView.setBackgroundResource(R.drawable.studi_sad);
            } else if (learnValue > 0) {
                //extrem kritischer Zustand
                //TODO Animation einfuegen?
                mStudiImageView.setBackgroundResource(R.drawable.studi_emergency);
            } else {
                // Studi stirbt
                // Deathscreen
                // Neustart
                //TODO Sound einfuegen?
                resetSharedPrefs();
                Context context = MainActivity.this;
                Class destinationActivity = DeathActivity.class;
                Intent intent = new Intent(context, destinationActivity);
                startActivity(intent);

            }
        }
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
     */
    private void checkState() {
        if (isSleeping) {
            mStudiImageView.setBackgroundResource(R.drawable.studi_sleeping);
            //Button Bild ändern zu "Aufwecken-Bild"
            mSleepButton.setImageResource(R.drawable.ic_wake_up);

            //Alle Buttons außer sleep deaktivieren
            disableButtons();
            mSleepButton.setEnabled(true);
            mSleepButton.setImageAlpha(0XFF);
        } else if (isEating) {
            isEating = false;
        } else if (isPartying) {
            mStudiImageView.setBackgroundResource(R.drawable.studi_partying);
            disableButtons();
            checkPartyStatus();
        } else if (isLearning) {
            checkLearnStatus();
        }
        updateImage();
    }


    private int getStudienTage() {
        long currentTime = System.currentTimeMillis();
        long timeAlive = currentTime - firstRunTime;

        studyDays = (int) (timeAlive / 1000 / 60 / 60 / 4);
        return studyDays;
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
        if (energyValue <= 30) {
            Toast.makeText(getApplicationContext(), "Du brauchst Energie zum Lernen - Iss doch etwas!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Bild ändern auf Animation lernen
        setAnimationLearn();
        //Alarm fuer Benachrichtigung starten
        startAlarm();

        learnValue += 30;
        energyValue -= 30;
        //10 Sekunden hinzufuegen, die das Lernen dauert -> LearnEndTime
        learnEndTime = System.currentTimeMillis() + 10 * gameSpeed;
        isLearning = true;
        updateLearnPb();
        updateEnergyPb();
        disableButtons();
    }

    private void feed() {
        //Bild aendern auf essen
        mStudiImageView.setBackgroundResource(R.drawable.studi_eating);
        isEating = true;
        energyValue += 10;
        updateEnergyPb();
        energyClickTime = System.currentTimeMillis();

        //Buttons deaktivieren
        disableButtons();

        //Alarm fuer Benachrichtigung starten
        startAlarm();
        //Handler ruft nach 3 Sekunden die naechste Methode auf
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                feedOver();
            }
        }, 5000);
    }

    private void feedOver() {
        //Studi-Bild entsprechend ändern
        checkState();
        isEating = false;
        learnEndTime = System.currentTimeMillis();

        //Buttons aktivieren
        enableButtons();
    }

    private void disableButtons() {
        mPartyButton.setEnabled(false);
        mSleepButton.setEnabled(false);
        mFeedButton.setEnabled(false);
        mLearnButton.setEnabled(false);
        mPartyButton.setImageAlpha(0X3F);
        mSleepButton.setImageAlpha(0X3F);
        mLearnButton.setImageAlpha(0X3F);
        mFeedButton.setImageAlpha(0X3F);
    }

    private void enableButtons() {
        mPartyButton.setEnabled(true);
        mSleepButton.setEnabled(true);
        mFeedButton.setEnabled(true);
        mLearnButton.setEnabled(true);
        mPartyButton.setImageAlpha(0XFF);
        mSleepButton.setImageAlpha(0XFF);
        mLearnButton.setImageAlpha(0XFF);
        mFeedButton.setImageAlpha(0XFF);
    }

    private void sleep() {
        if (!isSleeping) {
            //Bild aendern auf schlafen
            mStudiImageView.setBackgroundResource(R.drawable.studi_sleeping);
            isSleeping = true;
            sleepClickTime = System.currentTimeMillis();

            //Button Bild ändern zu "Aufwecken-Bild"
            mSleepButton.setImageResource(R.drawable.ic_wake_up);

            //Alle Buttons außer sleep deaktivieren
            disableButtons();
            mSleepButton.setEnabled(true);
            mSleepButton.setImageAlpha(0XFF);
        } else {
            long timeStudiSlept = System.currentTimeMillis() - sleepClickTime;
            energyValue += (timeStudiSlept / gameSpeed);
            updateEnergyPb();
            energyClickTime = System.currentTimeMillis();
            //Alarm fuer Benachrichtigung starten
            startAlarm();

            learnEndTime = System.currentTimeMillis();
            isSleeping = false;

            //Studi-Bild entsprechend ändern
            updateImage();
            //Button Bild ändern zu "Einschlafen-Bild"
            mSleepButton.setImageResource(R.drawable.ic_sleep);

            //Alle Buttons aktivieren
            enableButtons();
        }
    }

    private void party() {
        mStudiImageView.setBackgroundResource(R.drawable.studi_partying);
        isPartying = true;
        disableButtons();
        energyValue += 40;
        energyClickTime = System.currentTimeMillis() + 10 *gameSpeed;
        //während der Zeit, in der Party gemacht wird, werden keine Lernenpunkte abgezogen,
        //also learnClickTime auf Partyende setzen
        learnEndTime = System.currentTimeMillis() + 10 *gameSpeed;
        //Alarm fuer Benachrichtigung starten
        startAlarm();
        updateEnergyPb();
    }

    private void updateLearnPb() {
        if (learnValue > 100) {
            learnValue = 100;
        }else if(learnValue<0)
            learnValue=0;

        ObjectAnimator.ofInt(pbLearn, "progress", learnValue)
                .setDuration(800)
                .start();
        pbLeanText.setText(learnValue + "/" + pbLearn.getMax());
    }

    private void updateEnergyPb() {
        if (energyValue > 100) {
            energyValue = 100;
        } else if (energyValue < 0) {
            energyValue = 0;
        }
        ObjectAnimator.ofInt(pbEnergy, "progress", energyValue)
                .setDuration(800)
                .start();
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
        //Zeit, wann die Benachrichtigung nach druecken eines Buttons kommen soll.
        long timeInMillis = gameSpeed * 20;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAtButtonClick + timeInMillis, pendingIntent);

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
        isAppInForegeround = false;
        isUIThreadRunning = false;
        onPauseTime = System.currentTimeMillis();
        updateSharedPrefs();
    }

    /**
     * Musik stoppen, indem man den entsprechenden Service schließt
     */
    public void stopBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        stopService(intent);
        Toast.makeText(getApplicationContext(), "Stop Backgroundmusic", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBackgroundSound();
    }
}