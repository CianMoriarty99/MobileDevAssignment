package com.example.assignment112_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment112_1.model.PhotoData;
import com.example.assignment112_1.model.PhotoViewModel;
import com.example.assignment112_1.model.VisitData;
import com.example.assignment112_1.model.VisitPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods and properties for showing all visits on a Map.
 */

public class ViewVisitData extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private VisitData visitData;
    private List<PhotoData> photoList;
    private PhotoViewModel model;
    private List<LatLng> locsList = new ArrayList<>();
    private String visitTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);
        visitTitle = getIntent().getStringExtra("visit");

        setContentView(R.layout.activity_view_visit);
        TextView titleText = findViewById(R.id.path_title);
        TextView dateText = findViewById(R.id.path_date);

        photoList = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_visit_map);

        //Retrieve and observe photo data in U.I
        model.getVisitData().observe(this, visits -> {
            for (VisitData v: visits) {
                if (v.getTitle().equals(visitTitle)) {
                    visitData = v;
                    mapFragment.getMapAsync(this);
                    titleText.setText("Path Title: \n" + visitData.getTitle());
                    dateText.setText("Date: \n" + visitData.getDateTime());
                }
            }
        });

        //Retrieve and observe photo data in U.I
        model.getPhotoData().observe(this, photos -> {
            this.photoList = photos;
            mapFragment.getMapAsync(this);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (photoList.size() > 0) {
            for (PhotoData photo : photoList) {
                if (photo.getPathTitle() != null) {
                    if (photo.getPathTitle().equals(visitTitle)) {
                        float[] loc = photo.getLoc();
                        LatLng latLngLoc = new LatLng(loc[0], loc[1]);
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_camera_alt_24, getTheme());
                        drawable.setAlpha(255);
                        Canvas canvas = new Canvas();
                        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        canvas.setBitmap(bitmap);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        drawable.draw(canvas);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

                        MarkerOptions markerOptions = new MarkerOptions().position(latLngLoc)
                                .title("Photo")
                                //TODO Show photo thumbnail?
                                .alpha(1.0f)
                                .icon(icon);


                        Marker m = mMap.addMarker(markerOptions);
                        m.setTag(photo);
                    }
                }
            }
        }
        LatLng cameraLoc = null;
        if (visitData != null) {
            for (VisitPoint p : visitData.getPoints()) {

                float[] loc = p.getLocation();
                LatLng latLngLoc = new LatLng(loc[0], loc[1]);
                cameraLoc = latLngLoc;

                locsList.add(latLngLoc);
                MarkerOptions markerOptions = new MarkerOptions().position(latLngLoc)
                        .title("Pressure: " + p.getPressure() + "\nTemperature: " + p.getTemperature())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLoc, 25.0f));

            //Add path between points
            PolylineOptions lineOptions = new PolylineOptions()
                    .addAll(locsList)
                    .width(5)
                    .color(Color.RED);
            mMap.addPolyline(lineOptions);


        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() instanceof PhotoData) {
                    PhotoData imageData = (PhotoData) marker.getTag();
                    Intent i = new Intent(ViewVisitData.this, ViewImageData.class);
                    i.putExtra("img", imageData);
                    startActivity(i);
                }
                return false;
            }
        });
    }


}