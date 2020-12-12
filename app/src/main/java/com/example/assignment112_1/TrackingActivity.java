package com.example.assignment112_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment112_1.model.PhotoViewModel;
import com.example.assignment112_1.model.VisitData;
import com.example.assignment112_1.model.VisitPoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int ACCESS_FINE_LOCATION = 123;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button mButtonStop;
    private List<VisitPoint> pointsList;
    private PhotoViewModel model;
    private String title;
    private List<FileAndSense> images;
    private CommonSensor barometer;
    private CommonSensor thermometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        model = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);
        pointsList = new ArrayList<>();
        images =  new ArrayList<>();
        title = getIntent().getStringExtra("Title");

        mButtonStop = (Button) findViewById(R.id.button_stop);
        mButtonStop.setOnClickListener((view) -> {
            stopLocationUpdates();
            saveVisit();
            Intent intent = new Intent();
            intent.putExtra("exampleName", "exampleValue");
            setResult(RESULT_OK, intent);
            stopLocationUpdates();
            barometer.stopSensor();
            thermometer.stopSensor();
            finish();
        });
        mButtonStop.setEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener((view) -> {
            EasyImage.openCamera(TrackingActivity.this, 0);
        });

        barometer = new CommonSensor(this, Sensor.TYPE_PRESSURE, "Barometer");
        thermometer = new CommonSensor(this, Sensor.TYPE_AMBIENT_TEMPERATURE, "Thermometer");
    }

    private Location mCurrentLocation;
    private String mLastUpdateTime;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.d("MAP", "new location " + mCurrentLocation.toString());
            LatLng loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            if (mMap != null)
                mMap.addMarker(new MarkerOptions().position(loc)
                        .title(mLastUpdateTime));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
            float[] location = new float[]{(float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude()};

            VisitPoint visitPoint;
            if (thermometer.getSensorDataList().size() > 0 && barometer.getSensorDataList().size() > 0) {
                float temp = thermometer.getSensorDataList().get(thermometer.getSensorDataList().size() - 1);
                float pressure = barometer.getSensorDataList().get(barometer.getSensorDataList().size() - 1);
                visitPoint = new VisitPoint(location, temp, pressure);
            } else {
                visitPoint = new VisitPoint(location, null, null);
            }
            pointsList.add(visitPoint);
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void saveVisit() {
        Date currentDateTime = new Date();
        VisitData visit = new VisitData(title, currentDateTime, pointsList);
        model.insertVisitData(visit);
        for (FileAndSense image : images) {
            model.insertPhotoData(image.getFile(), title, image.getLoc(), image.getTemp(), image.getPressure());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        barometer.startSensing();
        thermometer.startSensing();
        startLocationUpdates();
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        barometer.stopSensor();
        thermometer.stopSensor();
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, null /* Looper */);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getApplicationContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    /**
     * save returned photos to the d.b and link with the visit
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
        for (File file : returnedPhotos) {

            Float[] loc = {(float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude()};
            Float temp;
            Float pressure;
            if (thermometer.getSensorDataList().size() > 0 && barometer.getSensorDataList().size() > 0) {
                temp = thermometer.getSensorDataList().get(thermometer.getSensorDataList().size() - 1);
                pressure = barometer.getSensorDataList().get(barometer.getSensorDataList().size() - 1);
            } else {
                pressure = null;
                temp = null;
            }

            FileAndSense fileAndLoc = new FileAndSense(file, loc, temp, pressure);
            images.add(fileAndLoc);

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_camera_alt_24, getTheme());
            Canvas canvas = new Canvas();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);


            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .title("Photo")
                    //TODO Show photo thumbnail?
                    .icon(icon);

            mMap.addMarker(markerOptions);
        }
    }

    public static final class FileAndSense {
        private final Float[] loc;
        private final File file;
        private final Float temp;
        private final Float pressure;

        public FileAndSense(File first, Float[] second, Float temp, Float pressure) {
            this.file = first;
            this.loc = second;
            this.temp = temp;
            this.pressure = pressure;
        }

        public Float[] getLoc() {
            return loc;
        }
        public File getFile() {
            return file;
        }
        public Float getTemp() {
            return temp;
        }
        public Float getPressure() {
            return pressure;
        }
    }
}
