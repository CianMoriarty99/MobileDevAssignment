package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment112_1.model.PhotoData;
import com.example.assignment112_1.model.VisitData;
import com.example.assignment112_1.model.VisitPoint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullView extends AppCompatActivity implements OnMapReadyCallback {


    private static final int ACCESS_FINE_LOCATION = 123;
    private static GoogleMap mMap;
    private static PhotoData img;
    private static List<VisitData> pointData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img = getIntent().getExtras().getParcelable("img");
        pointData = MainActivity.mVisitList;

        setContentView(R.layout.activity_full_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        ImageView imageView = findViewById(R.id.img_full);
        TextView titleText = findViewById(R.id.path_title);
        TextView descriptionText = findViewById(R.id.description);
        TextView temperatureText = findViewById(R.id.temperature);
        TextView pressureText = findViewById(R.id.pressure);


        imageView.setImageBitmap(BitmapFactory.decodeFile(img.getPhotoFile()));



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


        try {
            Float[] loc = img.getLoc();

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(loc[0], loc[1]))
                    .title("Test")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            Log.d("FULLVIEWMAP", markerOptions.toString());



            mMap.addMarker(markerOptions);
        }catch (Exception e){
            Log.d("FULLVIEWMAP", "No location");

        }

        List<LatLng> locsList = new ArrayList<>();
        for (VisitData data : pointData) {


            if(data.getTitle() == img.getPathTitle()){


                for (VisitPoint p : data.getPoints()){

                    Float[] loc = p.getLocation();
                    LatLng latLngLoc = new LatLng(loc[0], loc[1]);
                    locsList.add(latLngLoc);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLngLoc)
                            .title("Test")
                            .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mMap.addMarker(markerOptions);


                }


                //Add path between points
                PolylineOptions lineOptions = new PolylineOptions()
                        .addAll(locsList)
                        .width(5)
                        .color(Color.RED);
                mMap.addPolyline(lineOptions);



            }


        }


    }


}