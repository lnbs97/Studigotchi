package com.example.studigotchi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView mStudiImageView;
    private Button mLearnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateTimeInstance().format(calendar.getTimeInMillis());

        TextView textViewDate = findViewById(R.id.textView_date);
        textViewDate.setText(currentDate);

        mStudiImageView = findViewById(R.id.imageView_studi);
        mLearnButton = findViewById(R.id.button_learn);

        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn();
            }
        });
    }

    private void learn() {
        mStudiImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_studi_learning));
    }
}
