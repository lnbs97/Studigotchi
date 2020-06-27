package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class firstRunActivity extends AppCompatActivity {

    String name;
    EditText nameInput;
    Button enterButton;

    RadioGroup radioGroup;
    RadioButton radioButton;
    int gameSpeed=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        radioGroup = findViewById(R.id.radioGroup);

        enterButton = findViewById(R.id.button_mainActivity);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openMainActivity();
            }
        });

    }

    public void checkButton(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        switch(radioId) {
            case R.id.testGame:
                gameSpeed = 2000;
                    break;
            case R.id.normalGame:
                gameSpeed = 10000;
                break;
            case R.id.slowGame:
                gameSpeed = 100000;
                break;
        }
    }


    //erst namen auslesen, diesen mit der Konstanten uebergeben und MainActivity starten
    public void openMainActivity(){
        nameInput = findViewById(R.id.nameInput);
        name = nameInput.getText().toString();
        //checken, ob ein Name eingegeben wurde, ansonsten return
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Gib einen Namen ein", Toast.LENGTH_SHORT).show();
            return;
        }

        long firstRunTime = System.currentTimeMillis();

        //SharedPref und Editor aufrufen und namen speichern
        SharedPreferences mySPR = getSharedPreferences("file", 0);
        SharedPreferences.Editor editor = mySPR.edit();
        editor.putString("playerName", name)
                //Zeit des ersten Starts
        .putLong("firstRunTime", firstRunTime)
                //Spielgeschwindigkeit einstellen
        .putInt("gameSpeed", gameSpeed)
        .putLong("learnEndTime", System.currentTimeMillis())
        .putLong("energyClickTime",System.currentTimeMillis()).apply();


        //zurueck zur MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}