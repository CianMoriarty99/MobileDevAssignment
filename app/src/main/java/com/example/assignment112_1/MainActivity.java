package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
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
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.assignment112_1.model.PhotoData;
import com.example.assignment112_1.model.PhotoViewModel;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyAdapter.ImageListener, OnMapReadyCallback {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;
    private static final int ACCESS_FINE_LOCATION = 123;
    private Activity activity;
    private PhotoViewModel model;
    private List<PhotoData> myPictureList;
    private MyAdapter mAdapter;
    public static boolean sortByDate, sortByPath, listViewBool, mapBool;
    private GoogleMap mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //stop previous service.
        Intent myService = new Intent(MainActivity.this, LocationService.class);
        stopService(myService);

        setContentView(R.layout.activity_main);
        activity = this;
        model = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);

        // required by Android 6.0 +
        checkPermissions(getApplicationContext());
        initEasyImage();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        // set up the RecyclerView
        myPictureList = new ArrayList<>();
        mAdapter = new MyAdapter(myPictureList, this);
        mRecyclerView.setAdapter(mAdapter);

        if (listViewBool) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (mapBool) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }

        //Retrieve and observe photo data in U.I
        model.getPhotoData().observe(this, photos -> {
            myPictureList = photos;
            if (sortByDate) {
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
            mAdapter.setItems(photos);
            mAdapter.notifyDataSetChanged();
        });


        //Retrieve and observe photo data in U.I
        model.getVisitData().observe(this, visits -> {
        });

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openCamera(getActivity(), 0);
            }
        });

        Button visit_but = findViewById(R.id.button_visit);
        visit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VisitActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onImageClick(int position) {
        PhotoData imageData = myPictureList.get(position);
        ShowDialogBox(imageData);
    }

    public void ShowDialogBox(final PhotoData imageData) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_dialog);

        //Getting custom dialog views
        TextView Image_name = dialog.findViewById(R.id.txt_image_title);
        ImageView imageView = dialog.findViewById(R.id.img);
        EditText descriptionView = dialog.findViewById(R.id.txt_image_desc);
        Button btn_Close = dialog.findViewById(R.id.btn_close);
        Button btn_Save = dialog.findViewById(R.id.btn_save);

        String title = imageData.getPathTitle();

        Image_name.setText(title);

        descriptionView.setText(imageData.getDescription());


        new ImageHelper.ShowSingleImageTask().execute(new ImageHelper.FileAndView(new File(imageData.getPhotoFile()), imageView));

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionView.getText().toString();
                imageData.setDescription(description);
                model.updatePhotoData(imageData);
                dialog.dismiss();
            }
        });

        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FullView.class);
                i.putExtra("img", imageData);
                startActivity(i);
            }
        });

        dialog.show();
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
                Log.d("PICTURES", String.valueOf(listViewBool));
                this.recreate();


            case R.id.dateSort:
                sortByPath = false;
                sortByDate = !sortByDate;
                Log.d("PICTURES", String.valueOf(sortByDate));
                this.recreate();
                return true;

            case R.id.pathSort:
                sortByPath = !sortByPath;
                sortByDate = false;
                Log.d("PICTURES", String.valueOf(sortByPath));
                this.recreate();
                return true;


        }

        return super.onOptionsItemSelected(item);
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
     * save returned photos in d.b
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
        Log.d("NPHOTOS", returnedPhotos.toString());
        for (File file : returnedPhotos) {
            model.insertPhotoData(file);
        }
    }

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
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_camera_alt_24, getTheme());
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(25, 42, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, 25, 42);
        drawable.draw(canvas);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);


        initLocations();
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        
        try{

            for (PhotoData data : myPictureList) {
                if (data.getLoc()[0] != null) {
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLoc()[0], data.getLoc()[1]))
                            .title(data.getPathTitle())
                            //TODO Show photo thumbnail?
                            .icon(icon);

                    Marker m =  mMap.addMarker(markerOptions);
                    m.setTag(data);
                }
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    PhotoData data = (PhotoData) marker.getTag();
                    ShowDialogBox(data);
                    return false;
                }
            });
            
            
        }catch (Exception e){

            Log.d("ONMAPREADY", "No photo data");

        }

    }git
}