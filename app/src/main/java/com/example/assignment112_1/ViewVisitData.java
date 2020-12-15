package com.example.assignment112_1;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment112_1.model.PhotoViewModel;
import com.example.assignment112_1.model.VisitData;
import com.example.assignment112_1.model.VisitPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a display for one full image at a time.
 */

public class ViewVisitData extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private VisitData visitData;
    private PhotoViewModel model;
    private List<LatLng> locsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);
        String visitTitle = getIntent().getStringExtra("visit");

        setContentView(R.layout.activity_view_visit);
        TextView titleText = findViewById(R.id.path_title);
        TextView dateText = findViewById(R.id.path_date);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Retrieve and observe photo data in U.I
        model.getVisitData().observe(this, visits -> {
            for (VisitData v: visits) {
                if (v.getTitle().equals(visitTitle)) {
                    visitData = v;
                    mapFragment.getMapAsync(this);
                    titleText.setText("Path Title:" + visitData.getTitle());
                    dateText.setText("Path Title:" + visitData.getDateTime());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //TODO show photos on map

        if (visitData != null) {
            for (VisitPoint p : visitData.getPoints()) {

                Float[] loc = p.getLocation();
                LatLng latLngLoc = new LatLng(loc[0], loc[1]);

                locsList.add(latLngLoc);
                MarkerOptions markerOptions = new MarkerOptions().position(latLngLoc)
                        .title("Pressure: " + p.getPressure() + "\nTemperature: " + p.getTemperature())
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

            //TODO Centre on path
        }
    }


}