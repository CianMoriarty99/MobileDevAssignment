package com.example.assignment112_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides the user interface needed by the user to start tracking a visit.
 */

public class VisitActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "uk.ac.shef.oak.com4510.MESSAGE";
    public static final String EMPTY_STRING = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        // date and time to be shown to the user before starting a visit
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("EEE, dd MMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm:ss");

        TextView currentDate = (TextView) findViewById(R.id.current_date);
        TextView currentTime = (TextView) findViewById(R.id.current_time);

        currentDate.setText(formatDate.format(date));
        currentTime.setText(formatTime.format(date));
    }

    /**
     * Starts the visit by taking the provided title and starting the Tracking Activity. It sends
     * the title to the tracking activity so that collected data can be associated with the visit.
     * @param view
     */
    public void startVisit(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        EditText visit_title = (EditText) findViewById(R.id.visit_title);
        String title = visit_title.getText().toString();
        if (title.equals(EMPTY_STRING)) {
            Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("Title", title);
            startActivity(intent);
        }
    }
}
