package com.example.assignment112_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.example.assignment112_1.model.PhotoData;
import com.example.assignment112_1.model.PhotoViewModel;
import com.example.assignment112_1.model.VisitData;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.widget.Button;
import android.widget.TextView;


/**
 * This class is the starting point of the application. It contains the main gallery display as well
 * as buttons for the users to access the camera and to start recording a new visit. It also allows
 * the user to take photos without recording their visit.
 */

public class MainActivity extends AppCompatActivity implements ImageAdapter.ImageListener, OnMapReadyCallback, VisitAdapter.VisitListener {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;
    private static final int ACCESS_FINE_LOCATION = 123;
    private static final int ACCESS_COARSE_LOCATION = 123;
    private Activity activity;
    private PhotoViewModel model;
    private List<PhotoData> myPictureList;
    public static List<VisitData> myVisitList;
    private ImageAdapter mImageAdapter;
    private VisitAdapter mVisitAdapter;
    private RecyclerView mVisitRecyclerView, mImageRecyclerView;
    public static boolean sortByPath, listViewBool, mapBool;
    private GoogleMap mMap;
    public boolean device_has_camera;
    private TextView mTitleView;
    private CardView mCardView;
    private SupportMapFragment mMapFragment;
    private List<Integer> mySpanList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        activity = this;
        model = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);

        mCardView = findViewById(R.id.card_view);
        mTitleView = findViewById(R.id.view_title);
        mImageRecyclerView = findViewById(R.id.recycler_view);
        mVisitRecyclerView = findViewById(R.id.visit_recycler_view);
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        Button visit_but = findViewById(R.id.button_visit);

        listViewBool = true;
        mapBool = false;
        sortByPath = true;
        mySpanList = new ArrayList<>();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        // required by Android 6.0 +
        checkPermissions(getApplicationContext());
        initEasyImage();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);
        mMapFragment.getMapAsync(this);


        // set up the RecyclerView
        myPictureList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(myPictureList, this);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        myVisitList = new ArrayList<>();
        mVisitAdapter = new VisitAdapter(myVisitList, this);
        mVisitRecyclerView.setAdapter(mVisitAdapter);
        mVisitRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTitleView.setText("Browse Visits: ");
        mVisitRecyclerView.setVisibility(View.VISIBLE);
        mImageRecyclerView.setVisibility(View.INVISIBLE);
        mTitleView.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.INVISIBLE);
        mMapFragment.getView().setVisibility(View.INVISIBLE);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyCJ97ya3k69cM_Dkp0rd8ZiF4hY1ubKxHw");

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));



        //Retrieve and observe photo data in U.I
        model.getPhotoData().observe(this, photos -> {
            myPictureList = photos;
            sortImageList();
            mImageAdapter.setImages(photos);
            mImageAdapter.notifyDataSetChanged();
        });


        //Retrieve and observe photo data in U.I
        model.getVisitData().observe(this, visits -> {
            myVisitList = visits;
            mVisitAdapter.setVisits(visits);
            mVisitAdapter.notifyDataSetChanged();
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });


        PackageManager pm = this.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EasyImage.openCamera(getActivity(), 0);
                }
            });
        }
        else{

            fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }

        visit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VisitActivity.class);
                startActivity(intent);
            }
        });


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (place != null)
                    // TODO: Get info about the selected place.
                    Log.i("SEARCHBAR", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                if (status != null)
                    // TODO: Handle the error.
                    Log.i("SEARCHBAR", "An error occurred: " + status);
            }
        });

    }

    @Override
    public void onImageClick(int position) {
        PhotoData imageData = myPictureList.get(position);
        Intent i = new Intent(MainActivity.this, ViewImageData.class);
        i.putExtra("img", imageData);
        startActivity(i);
    }

    @Override
    public void onVisitClick(int position) {
        VisitData visitData = myVisitList.get(position);
        Intent i = new Intent(MainActivity.this, ViewVisitData.class);
        i.putExtra("visit", visitData.getTitle());
        startActivity(i);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_men, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.changeView:
                if (!listViewBool & mapBool) {
                    listViewBool = true;
                    mapBool = false;
                } else if (!listViewBool & !mapBool) {
                    mapBool = true;
                } else {
                    listViewBool = false;
                }
                if (listViewBool) {
                    mTitleView.setText("Browse Visits: ");
                    mVisitRecyclerView.setVisibility(View.VISIBLE);
                    mImageRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    mTitleView.setText("Browse Photos: ");
                    mImageRecyclerView.setVisibility(View.VISIBLE);
                    mVisitRecyclerView.setVisibility(View.INVISIBLE);
                }
                if (mapBool) {
                    mTitleView.setVisibility(View.INVISIBLE);
                    mCardView.setVisibility(View.VISIBLE);
                    mMapFragment.getView().setVisibility(View.VISIBLE);
                    mImageRecyclerView.setVisibility(View.INVISIBLE);
                    mVisitRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    mTitleView.setVisibility(View.VISIBLE);
                    mCardView.setVisibility(View.INVISIBLE);
                    mMapFragment.getView().setVisibility(View.INVISIBLE);
                }

            case R.id.dateSort:
                sortByPath = false;
                sortImageList();
                mImageAdapter.notifyDataSetChanged();


            case R.id.pathSort:
                sortByPath = true;
                sortImageList();
                mImageAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortImageList() {
        if (!sortByPath) {
            Collections.sort(myPictureList, (d1, d2) -> {
                return d2.getId() - d1.getId();
            });
        } else {
            Collections.sort(myPictureList, (d1, d2) -> {
                return d1.getId() - d2.getId();
            });
        }
        if(sortByPath){
            Collections.sort(myPictureList, (d1, d2) -> {
                try {
                    String path1 = d1.getPathTitle();
                    String path2 = d2.getPathTitle();

                    int compare = path1.compareTo(path2);
                    Log.d("SORTBYPATH", String.valueOf(compare));
                    return compare;
                }
                catch (Exception e) {
                    Log.d("SORTBYPATH", "0");
                    return -1;
                }
            });
        }
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    /**
     * Saves photos taken by the user to the database.
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
        Log.d("NPHOTOS", returnedPhotos.toString());
        for (File file : returnedPhotos) {
            model.insertPhotoData(file);
        }
    }

    /**
     * Checks that the necessary permissions to run the app are granted. These permissions are to
     * read and write to external storage for the user to be able to take save taken photos to the
     * phone's gallery. Other permissions are needed to access the location and sensors.
     * @param context this Activity
     */
    public void checkPermissions(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                }

            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Writing external storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }

            }
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

            }

            return;
        }
    }

    /**
     * Iinitialises the EasyImage library to allow taking and saving photos.
     */
    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }


    public Activity getActivity() {
        return activity;
    }

    private void initLocations() {
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
                        123);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted so restart location service:
                    mMap.setMyLocationEnabled(true);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        initLocations();

        try{

            for (PhotoData data : myPictureList) {
                if (data.getLoc().length > 0) {
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(ImageHelper.createMapsMarker(this, new File(data.getPhotoFile())));

                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLoc()[0], data.getLoc()[1]))
                            .title(data.getPathTitle())
                            .alpha(1.0f)
                            .icon(icon);

                    Marker m =  mMap.addMarker(markerOptions);
                    m.setTag(data);
                }
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    PhotoData imageData = (PhotoData) marker.getTag();
                    Intent i = new Intent(MainActivity.this, ViewImageData.class);
                    i.putExtra("img", imageData);
                    startActivity(i);
                    return false;
                }
            });
            
            
        }catch (Exception e){

            Log.d("ONMAPREADY", "No photo data");

        }

    }
}