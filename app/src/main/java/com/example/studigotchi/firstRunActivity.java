package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class firstRunActivity extends AppCompatActivity {

    String name;
    EditText nameInput;
    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        enterButton = findViewById(R.id.button_mainActivity);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openMainActivity();
            }
        });

    }

    //erst namen auslesen, diesen mit der Konstanten uebergeben und MainActivity starten
    public void openMainActivity(){
        nameInput = findViewById(R.id.nameInput);
        name = nameInput.getText().toString();
        //checken, ob ein Name eingegeben wurde, ansonsten return
        if (name.isEmpty()) {
            return;
        }

        //SharedPref und Editor aufrufen und namen speichern
        SharedPreferences mySPR = getSharedPreferences("file", 0);
        SharedPreferences.Editor editor = mySPR.edit();
        editor.putString("playerName", name).commit();

        // Zeit des ersten Starts
        long firstRunTime = System.currentTimeMillis();
        editor.putLong("firstRunTime", firstRunTime).commit();

        //zurueck zur MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}