package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment112_1.model.PhotoData;
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

public class ViewImageData extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static List<VisitData> pointData;
    private PhotoData photoData;
    private PhotoViewModel model;
    private EditText descriptionText;
    private List<LatLng> locsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointData = MainActivity.mVisitList;
        model = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);
        photoData = getIntent().getExtras().getParcelable("img");


        setContentView(R.layout.activity_view_image);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_visit_map);
        mapFragment.getMapAsync(this);


        ImageView imageView = findViewById(R.id.img_full);
        TextView titleText = findViewById(R.id.path_title);
        TextView temperatureText = findViewById(R.id.temperature);
        TextView pressureText = findViewById(R.id.pressure);
        descriptionText = findViewById(R.id.txt_image_desc);

        imageView.setImageBitmap(BitmapFactory.decodeFile(photoData.getPhotoFile()));

        try{
            if(photoData.getPathTitle() != null)
                titleText.setText("Path Title:" + photoData.getPathTitle());

        }catch (Exception e){
            Log.d("PHOTODATA", "No title");
        }

        try{
            if(photoData.getDescription() != null)
                descriptionText.setText(photoData.getDescription());

        }catch (Exception e){
            Log.d("PHOTODATA", "No description");
        }

        try{
            temperatureText.setText("Temperature:" + photoData.getTemperature().toString());

        }catch (Exception e){
            Log.d("PHOTODATA", "No temperature");
        }

        try{
            pressureText.setText("Pressure:" + photoData.getPressure().toString());

        }catch (Exception e){
            Log.d("PHOTODATA", "No pressure");
        }


        //Saves the description when the edit-text is unselected.
        descriptionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String description = descriptionText.getText().toString();
                    photoData.setDescription(description);
                    model.updatePhotoData(photoData);
                }
            }
        });



        //TODO get fullscreen image
       /* imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FullView.class);
                i.putExtra("img", imageData);
                startActivity(i);
            }
        });
        */


    }

    //Save description when the back button is pressed.
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        String description = descriptionText.getText().toString();
        Log.e("Back", "Back! "+description);
        photoData.setDescription(description);
        Log.e("Back2", "Back! "+photoData.getDescription());
        model.updatePhotoData(photoData);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);


        try {



            float[] location = photoData.getLoc();
            LatLng loc = new LatLng(location[0], location[1]);
            MarkerOptions markerOptions = new MarkerOptions().position(loc)
                    .title("Test")
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            Log.d("FULLVIEWMAP", markerOptions.toString());



            mMap.addMarker(markerOptions);

            // it centres the camera around the new location and zooms in
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));
        }catch (Exception e){
            Log.d("FULLVIEWMAP", "No location");

        }


        for (VisitData data : pointData) {
            Log.d("VisitData", data.getTitle());
            Log.d("photoData", photoData.getPathTitle());

            if(data.getTitle() == photoData.getPathTitle()){

                for (VisitPoint p : data.getPoints()){

                    Log.d("VisitPoint", p.toString());

                    Float[] loc = p.getLocation();
                    LatLng latLngLoc = new LatLng(loc[0], loc[1]);
                    Log.d("latlng", latLngLoc.toString());
                    locsList.add(latLngLoc);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLngLoc)
                            .title("Test")
                            .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mMap.addMarker(markerOptions);


                }




            }


        }
        Log.d("locslist", locsList.toString());
        //Add path between points
        PolylineOptions lineOptions = new PolylineOptions()
                .addAll(locsList)
                .width(5)
                .color(Color.RED);
        mMap.addPolyline(lineOptions);



    }


}