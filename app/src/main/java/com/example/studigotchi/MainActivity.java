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
import android.os.Handler;
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
    private ProgressBar pbHorizontal;
    private ProgressBar pbEnergy;
    private TextView pbText;

    /* SharedPreferences Variablen START */

    private long onPauseTime;
    private long firstRunTime;

    private long learnClickTime;
    private long energyClickTime;
    private long eatClickTime;
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
    /* SharedPreferences Variablen ENDE */

    private void getSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", 0);

        onPauseTime = sharedPreferences.getLong("onPauseTime", 0);
        firstRunTime = sharedPreferences.getLong("firstRunTime", 0);

        learnClickTime = sharedPreferences.getLong("learnClickTime", System.currentTimeMillis());
        eatClickTime = sharedPreferences.getLong("eatClickTime", System.currentTimeMillis());
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
                .putLong("eatClickTime", eatClickTime)
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
                .putInt("studyDays", 0).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer mpLearnSound = MediaPlayer.create(this, R.raw.learn_sound);
        final MediaPlayer mpButtonSound = MediaPlayer.create(this, R.raw.button_press);
        final MediaPlayer mpSleepSound = MediaPlayer.create(this, R.raw.sleep_sound);
        final MediaPlayer mpPartySound = MediaPlayer.create(this, R.raw.party_sound);
        final MediaPlayer mpFeedSound = MediaPlayer.create(this, R.raw.feed_sound);
        final MediaPlayer mpWakeUpSound = MediaPlayer.create(this, R.raw.wake_up_sound);
        final MediaPlayer mpYawningSound = MediaPlayer.create(this, R.raw.yawning_sound);

        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        pbHorizontal = findViewById(R.id.pbHorizontal);
        pbEnergy = findViewById(R.id.pbEnergy);
        pbText = findViewById(R.id.pbText);

        // get studi image
        mStudiImageView = findViewById(R.id.imageView_studi);


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


    protected void startProg() {
        long currentTime = System.currentTimeMillis();

        /*Studigotchi ist am lernen, Background Animation wird auf Lernen gesetzt,
         * wenn 10 Sekunden seit learnClickTime vergangen sind*/
        if (isLearning && currentTime <= learnClickTime) {
            setAnimationLearn();
            disableButtons();
        }
        //Wenn der Studi fertig gerlernt hat
        if (isLearning && currentTime >= learnClickTime) {
            isLearning = false;
            enableButtons();
        }
        //Wenn der Studi nicht mehr lernt, und Zeit seit lernen vergangen ist, Punktabzug
        //Außerdem wird das Bild durch checkState geprüft und ggf. abgeändert
        if (!isLearning && currentTime > learnClickTime) {

            learnValue -= 0.5 * ((System.currentTimeMillis() - learnClickTime) / 1000);
            energyValue -= 0.5 * ((System.currentTimeMillis() - energyClickTime) / 1000);
            learnClickTime = System.currentTimeMillis();
            energyClickTime = System.currentTimeMillis();
        }
        updateLearnPb();
        updateEnergyPb();

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

        if (isSleeping) {
            mStudiImageView.setBackgroundResource(R.drawable.studi_sleeping);
            isSleeping = true;
            //Button Bild ändern zu "Aufwecken-Bild"
            mSleepButton.setImageResource(R.drawable.ic_wake_up);

            //Alle Buttons außer sleep deaktivieren
            disableButtons();
            mSleepButton.setEnabled(true);
            mSleepButton.setImageAlpha(0XFF);
        } else if (isEating) {
            isEating = false;
            startProg();
        } else if (isPartying) {
            checkPartyStatus();
            startProg();
        } else if (!isFirstRun)
            startProg();
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
            startProg();
        } else if (isPartying) {
            mStudiImageView.setBackgroundResource(R.drawable.studi_partying);
            disableButtons();
            checkPartyStatus();
            if (!isPartying) {
                startProg();
            }
        } else if (isLearning) {
            startProg();
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

        //TODO test9999

        learnValue += 30;
        energyValue -= 30;
        //TODO Logik / Name von LearnClickTime ueberdenken
        //10 Sekunden hinzufuegen, die das Lernen dauert ->
        // LearnClickTime ist eigtl falscher Name für die Logik
        learnClickTime = System.currentTimeMillis() + 10000;
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
        }, 3000);
    }

    private void feedOver() {
        //Studi-Bild entsprechend ändern
        checkState();
        isEating = false;

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
            long timeStudiWasSleeping = System.currentTimeMillis() - sleepClickTime;
            energyValue += 0.5 * (timeStudiWasSleeping / 1000);
            updateEnergyPb();
            energyClickTime = System.currentTimeMillis();
            //Alarm fuer Benachrichtigung starten
            startAlarm();

            learnClickTime = System.currentTimeMillis();
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
        energyClickTime = System.currentTimeMillis() + 10000;
        //während der Zeit, in der Party gemacht wird, werden keine Lernenpunkte abgezogen,
        //also learnClickTime auf Partyende setzen
        learnClickTime = System.currentTimeMillis() + 10000;
        //Alarm fuer Benachrichtigung starten
        startAlarm();
        updateEnergyPb();
    }

    private void updateLearnPb() {
        if (learnValue > 100) {
            learnValue = 100;
        }
        ObjectAnimator.ofInt(pbHorizontal, "progress", learnValue)
                .setDuration(2000)
                .start();
        pbText.setText(learnValue + "/" + pbHorizontal.getMax());
    }

    private void updateEnergyPb() {
        if (energyValue > 100) {
            energyValue = 100;
        } else if (energyValue < 0) {
            energyValue = 0;
        }
        ObjectAnimator.ofInt(pbEnergy, "progress", energyValue)
                .setDuration(2000)
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
        //zu Testzwecken gibt es eine Benachrichtigung nach 20 Sekunden klicken des Buttons
        long oneHourInMillis = 1000 * 20;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAtButtonClick + oneHourInMillis, pendingIntent);

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