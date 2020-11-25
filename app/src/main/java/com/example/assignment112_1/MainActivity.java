package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
 
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.assignment112_1.model.PhotoData;
import com.example.assignment112_1.model.PhotoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;

    private Activity activity;
    private PhotoViewModel model;
    private List<PhotoData> myPictureList;
    private  MyAdapter mAdapter;


    ArrayList<Integer> mImageIds = new ArrayList < >(Arrays.asList(
            R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,
            R.drawable.img5,R.drawable.img6,R.drawable.img7
            ));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity= this;
        model = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(PhotoViewModel.class);

        // required by Android 6.0 +
        checkPermissions(getApplicationContext());
        initEasyImage();
      
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        // set up the RecyclerView
        myPictureList = new ArrayList<>();
        mAdapter = new MyAdapter(myPictureList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        //Retrieve and observe photo data in U.I
        model.getPhotoData().observe(this, photos-> {
            myPictureList = photos;
            mAdapter.setItems(photos);
            mAdapter.notifyDataSetChanged();
            Log.d("NPHOTOS", mAdapter.getItems().toString());
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

        GridView gridView = findViewById(R.id.myGrid);
        gridView.setAdapter(new ImageAdaptor(mImageIds,this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int item_pos = mImageIds.get(position);

                ShowDialogBox(item_pos);
            }
        });
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
     * add to the grid
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
        Log.d("NPHOTOS", returnedPhotos.toString());
        for (File file : returnedPhotos) {
           PhotoData photo = new PhotoData(file.toString(), file.toString());
           model.insertPhotoData(photo);
        }
    }

    private void checkPermissions(final Context context) {
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
          
    public void ShowDialogBox(final int item_pos){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_dialog);

        //Getting custom dialog views
        TextView Image_name = dialog.findViewById(R.id.txt_Image_name);
        ImageView Image = dialog.findViewById(R.id.img);
        Button btn_Full = dialog.findViewById(R.id.btn_full);
        Button btn_Close = dialog.findViewById(R.id.btn_close);

        String title = getResources().getResourceName(item_pos);

        //extracting name
        int index = title.indexOf("/");
        String name = title.substring(index+1,title.length());
        Image_name.setText(name);

        Image.setImageResource(item_pos);

        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_Full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FullView.class);
                i.putExtra("img_id", item_pos);
                startActivity(i);
            }
        });

        dialog.show();

    }
}