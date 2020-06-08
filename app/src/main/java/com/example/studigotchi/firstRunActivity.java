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
    // TODO Hier muss noch abgefangen werden, falls kein Name eingegeben wurde, und trotzdem enter gedrueckt wurde
    public void openMainActivity(){
        nameInput = findViewById(R.id.nameInput);
        name = nameInput.getText().toString();
        showToast(name);

        //SharedPref und Editor aufrufen und namen speichern
        SharedPreferences mySPR = getSharedPreferences("mySPRFILE", 0);
        SharedPreferences.Editor editor = mySPR.edit();
        editor.putString("name", name).commit();

        //Restliche Werte auf Startwert setzen
        editor.putInt("health", 100);
        editor.putInt("studientage", 0);
        // Zeit des erstens Starts
        long firstRunTime = System.currentTimeMillis();
        editor.putLong("firstRunTime", firstRunTime).commit();

        //zurueck zur MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //kleine Anzeige des Namens unten im Screen
    private void showToast(String toast){
        Toast.makeText(firstRunActivity.this, toast, Toast.LENGTH_SHORT).show();
    }
}