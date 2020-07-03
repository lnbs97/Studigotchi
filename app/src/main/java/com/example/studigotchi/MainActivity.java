package com.example.studigotchi;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private ImageView mStudiImageView;
    private ImageButton mLearnButton;
    private ImageButton mFeedButton;
    private ImageButton mSleepButton;
    private ImageButton mPartyButton;
    private Boolean musicIsPlaying;
    private static final String LOG_TAG = "MainActivity";
    private AnimationDrawable backgroundAnimation;
    private ProgressBar pbLearn;
    private ProgressBar pbEnergy;
    private TextView pbLearnText;
    private TextView pbEnergyText;
    private TextView mStudyDaysText;

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
    private String highscoreName;

    private int learnValue;
    private int energyValue;
    private int studyDays;
    private int highscoreDays;
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
        highscoreName = sharedPreferences.getString("highscoreName", null);

        learnValue = sharedPreferences.getInt("learnValue", 70);
        energyValue = sharedPreferences.getInt("energyValue", 70);
        studyDays = sharedPreferences.getInt("studyDays", 0);
        highscoreDays = sharedPreferences.getInt("highscoreDays", 0);

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
                .putString("highscoreName", highscoreName)
                .putInt("learnValue", learnValue)
                .putInt("energyValue", energyValue)
                .putInt("studyDays", studyDays)
                .putInt("highscoreDays", highscoreDays).apply();
    }

    private void resetSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        firstRunTime = System.currentTimeMillis();

        isFirstRun = true;
        learnValue = 70;
        energyValue = 70;
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
                .putInt("energyValue", energyValue)
                .putInt("studyDays", 0)
                .putInt("gameSpeed", 2000).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            // Wenn Sound Button gedrückt wurde
            case R.id.action_sound:
                toggleMusic();
                if (musicIsPlaying) {
                    item.setIcon(R.drawable.volume_on);
                } else {
                    item.setIcon(R.drawable.volume_off);
                }
                return true;

            // Wenn Info Button gedrückt wurde
            case R.id.action_info:
                openInfoActivity();
                return true;

            // Wenn Hilfe Button gedrückt wurde
            case R.id.action_help:
                openHelpActivity();
                return true;

            // Wenn Exmatrikulations Button gedrückt wurde
            case R.id.action_restart:
                restart();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Verhindert, dass man nach dem Erstellen eines Studis zurückspringen kann
     */

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppInForegeround = true;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Get progress bar
        pbLearn = findViewById(R.id.pbHorizontal);
        pbEnergy = findViewById(R.id.pbEnergy);
        pbLearnText = findViewById(R.id.pbLearnText);
        pbEnergyText = findViewById(R.id.pbEnergyText);

        // get studi image
        mStudiImageView = findViewById(R.id.imageView_studi);

        mStudyDaysText = findViewById(R.id.tv_studientage);
        mStudyDaysText.setText("Tag " + studyDays);

        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);
        gameSpeed = sharedPreferences.getInt("gameSpeed", 2000);

        startUIThread();
        isUIThreadRunning = true;

        // wenn User auf den Lernen-Button klickt
        // Bilder aendern und Sound starten
        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();
                mpButtonSound.start();
                mpLearnSound.start();
            }
        });
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feed();
                mpButtonSound.start();
                mpFeedSound.start();
            }
        });
        mSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                party();
                mpButtonSound.start();
                mpPartySound.start();
            }
        });
    }

    private void startUIThread() {
        if (!isUIThreadRunning) {
            Thread updateUIThread = new Thread() {
                @Override
                public void run() {
                    while (isAppInForegeround) {
                        try {
                            sleep(1000);

                            if (!isLearning && !isSleeping && !isEating && !isPartying) {
                                updateLearnValue();
                                updateEnergyValue();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isLearning || isPartying)
                                        checkState();
                                    updateStudyDays();
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


    private void updateStudyDays() {
        // Studientage anzeigen lassen

        long currentTime = System.currentTimeMillis();
        long timeAlive = currentTime - firstRunTime;

        studyDays = (int) (timeAlive / 1000 / 60 / 10);

        mStudyDaysText.setText("Tag " + studyDays);
    }

    private void checkLearnStatus() {
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
        int energyLost = (int) ((currentTime - energyClickTime) / (gameSpeed));
        Log.i(LOG_TAG, "energyClickTime: " + energyClickTime);
        if (energyLost > 0) {
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
            int learnLost = (int) ((currentTime - learnEndTime) / (gameSpeed));
            if (learnLost > 0) {
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

    private void setAnimationOver50() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_more50_idle);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationUnder10() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_under10_idle);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationUnder50() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_under50_idle);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationSleeping() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_sleeping);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationParty() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_party);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    private void setAnimationEating() {
        mStudiImageView.setBackgroundResource(R.drawable.animation_eating);
        backgroundAnimation = (AnimationDrawable) mStudiImageView.getBackground();
        backgroundAnimation.start();
    }

    //beim Öffnen der App wird timestamp ausgelesen und timestamp gespeichert
    protected void onResume() {
        super.onResume();
        getSharedPrefs();
        cancelAlarm();

        //Abfrage, ob App das erste mal aufgerufen wurde
        if (isFirstRun) {
            //first RunActivity aufrufen, um Studinamen zu vergeben
            openFirstRunActivity();
        }

        isAppInForegeround = true;
        startUIThread();
        isUIThreadRunning = true;
        playBackgroundSound();

        //set up StudiImage
        checkState();

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
                setAnimationHappy();
                //mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_happy));
            } else if (learnValue >= 50) {
                //Setze normales Bild und sounds
                setAnimationOver50();
            } else if (learnValue > 10) {
                // Setze unglückliches bild und sound
                setAnimationUnder50();
            } else if (learnValue > 0) {
                //extrem kritischer Zustand
                setAnimationUnder10();
            } else {
                // Neustart
                restart();
            }
        }
    }

    /**
     * Ein neues Spiel starten
     * Setzt alle Werte zurück und springt zur DeathActivity.
     */
    private void restart() {
        //Prüfen ob ein neuer highscore erreicht wurde
        if (studyDays >= highscoreDays) {
            highscoreDays = studyDays;
            highscoreName = playerName;
            updateSharedPrefs();
        }
        resetSharedPrefs();
        Context context = MainActivity.this;
        Class destinationActivity = DeathActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("studyDays", studyDays);
        startActivity(intent);
    }

    public void playBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        startService(intent);
        musicIsPlaying = true;
        //TODO Sound icon auf eingeschaltet setzen
    }


    /**
     * Aktualisiert die App und den Studi basierend auf seinem Leben
     * Wird bei jedem Öffnen ausgeführt
     */
    private void checkState() {
        if (isSleeping) {
            setAnimationSleeping();
            //Button Bild ändern zu "Aufwecken-Bild"
            mSleepButton.setImageResource(R.drawable.ic_wake_up);

            //Alle Buttons außer sleep deaktivieren
            disableButtons();
            mSleepButton.setEnabled(true);
            mSleepButton.setImageAlpha(0XFF);
        } else if (isEating) {
            isEating = false;
        } else if (isPartying) {
            setAnimationParty();
            disableButtons();
            checkPartyStatus();
        } else if (isLearning) {
            checkLearnStatus();
        }
        updateImage();
    }

    /**
     * Wird aufgerufen, wenn der music-button gedrueckt wird.
     * Die Musik wird an bzw ausgeschaltet und
     * das icon des Buttons gewechselt dementsprechend
     */
    private void toggleMusic() {
        if (musicIsPlaying) {
            stopBackgroundSound();
            musicIsPlaying = false;
        } else {
            playBackgroundSound();
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
        setAnimationEating();
        isEating = true;
        energyValue += 10;
        updateEnergyPb();
        energyClickTime = System.currentTimeMillis();

        //Buttons deaktivieren
        disableButtons();

        //Handler ruft nach 3 Sekunden die naechste Methode auf
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                feedOver();
            }
        }, 3000);
    }

    private void feedOver() {
        //Studi-Bild entsprechend ändern
        checkState();
        isEating = false;
        learnEndTime = System.currentTimeMillis();

        //Buttons aktivieren
        enableButtons();
    }

    /**
     * Deaktiviert alle Buttons
     */
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

    /**
     * Aktiviert alle Buttons
     */
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
            setAnimationSleeping();
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
            learnValue -= ((timeStudiSlept / gameSpeed)/2);
            updateEnergyPb();
            energyClickTime = System.currentTimeMillis();

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
        setAnimationParty();
        isPartying = true;
        disableButtons();
        energyValue += 40;
        energyClickTime = System.currentTimeMillis() + 10 * gameSpeed;
        //während der Zeit, in der Party gemacht wird, werden keine Lernenpunkte abgezogen,
        //also learnClickTime auf Partyende setzen
        learnEndTime = System.currentTimeMillis() + 10 * gameSpeed;
        updateEnergyPb();
    }

    private void updateLearnPb() {
        if (learnValue > 100) {
            learnValue = 100;
        } else if (learnValue < 0)
            learnValue = 0;

        ObjectAnimator.ofInt(pbLearn, "progress", learnValue)
                .setDuration(800)
                .start();
        pbLearnText.setText(learnValue + "/" + pbLearn.getMax());
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
        pbEnergyText.setText(energyValue + "/" + pbEnergy.getMax());
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
            String description = "This is channel 1";
            NotificationChannel channelLearn = new NotificationChannel("notifyLearn", "LEARN", NotificationManager.IMPORTANCE_DEFAULT);
            channelLearn.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channelLearn);
            }
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

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAtButtonClick + timeInMillis, pendingIntent);
        }

    }

    private void cancelAlarm() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

    /**
     * openInfoActivity oeffnet InfoActivity und ermoeglicht dem User, Infos ueber die App zu bekommen
     */
    private void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    /**
     * Öffnet die Hilfe Activity
     */
    private void openHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
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
        /*Alarm setzen*/
        startAlarm();
        isAppInForegeround = false;
        isUIThreadRunning = false;
        onPauseTime = System.currentTimeMillis();
        updateSharedPrefs();
        stopBackgroundSound();
    }

    /**
     * Musik stoppen, indem man den entsprechenden Service schließt
     */
    public void stopBackgroundSound() {
        Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
        stopService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBackgroundSound();
    }
}