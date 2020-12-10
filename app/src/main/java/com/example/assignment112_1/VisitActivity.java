package com.example.assignment112_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VisitActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "uk.ac.shef.oak.com4510.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("EEE, dd MMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm:ss");

        TextView currentDate = (TextView) findViewById(R.id.current_date);
        TextView currentTime = (TextView) findViewById(R.id.current_time);

        currentDate.setText(formatDate.format(date));
        currentTime.setText(formatTime.format(date));
    }

    public void startVisit(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        EditText visit_title = (EditText) findViewById(R.id.visit_title);
        String title = visit_title.getText().toString();
        intent.putExtra("Title", title);
        startActivity(intent);
    }
}
