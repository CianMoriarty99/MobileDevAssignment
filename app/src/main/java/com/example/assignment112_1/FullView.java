package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment112_1.model.PhotoData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class FullView extends AppCompatActivity implements OnMapReadyCallback {


    private static final int ACCESS_FINE_LOCATION = 123;
    private static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            setContentView(R.layout.activity_full_view);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.d("FULLVIEWMAP", "No Map");
        }


        ImageView imageView = findViewById(R.id.img_full);
        TextView titleText = findViewById(R.id.path_title);
        TextView descriptionText = findViewById(R.id.description);
        TextView temperatureText = findViewById(R.id.temperature);
        TextView pressureText = findViewById(R.id.pressure);


        PhotoData img = getIntent().getExtras().getParcelable("img");
        imageView.setImageBitmap(BitmapFactory.decodeFile(img.getPhotoFile()));

        Log.d("FULLVIEWMAP", img.toString());

        try{
            if(img.getPathTitle() != null)
                titleText.setText("Path Title:" + img.getPathTitle());

        }catch (Exception e){
            Log.d("PHOTODATA", "No title");
        }

        try{
            if(img.getDescription() != null)
                descriptionText.setText("Description:" + img.getDescription());

        }catch (Exception e){
            Log.d("PHOTODATA", "No description");
        }

        try{
            temperatureText.setText("Temperature:" + img.getTemperature().toString());

        }catch (Exception e){
            Log.d("PHOTODATA", "No temperature");
        }

        try{
            pressureText.setText("Pressure:" + img.getPressure().toString());

        }catch (Exception e){
            Log.d("PHOTODATA", "No pressure");
        }









    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


}