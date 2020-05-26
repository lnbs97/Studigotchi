package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateTimeInstance().format(calendar.getTimeInMillis());

        TextView textViewDate = findViewById(R.id.textView_date);
        textViewDate.setText(currentDate);
    }
}
