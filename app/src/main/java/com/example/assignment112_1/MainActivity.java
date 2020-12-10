package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
 
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyAdapter.ImageListener {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;
    private Activity activity;
    private PhotoViewModel model;
    private List<PhotoData> myPictureList;
    private  MyAdapter mAdapter;
    public static boolean sortByDate, sortByPath, listViewBool;



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
        mAdapter = new MyAdapter(myPictureList, this);
        mRecyclerView.setAdapter(mAdapter);

        if(listViewBool)
        {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


        //Retrieve and observe photo data in U.I
        model.getPhotoData().observe(this, photos-> {
            //TODO check location data works
            myPictureList = photos;





            if(sortByDate){

                Collections.sort(myPictureList, (d1, d2) -> {
                    return d2.getId() - d1.getId();
                });

            }else {

                Collections.sort(myPictureList, (d1, d2) -> {
                    return d1.getId() - d2.getId();
                });
            }

            mAdapter.setItems(photos);
            mAdapter.notifyDataSetChanged();
            Log.d("NPHOTOS", mAdapter.getItems().toString());
        });

        //Retrieve and observe photo data in U.I
        model.getVisitData().observe(this, visits-> {
            Log.d("NVISITS", visits.toString());
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

        FloatingActionButton fab_2 = (FloatingActionButton) findViewById(R.id.fab_visit);
        fab_2.setOnClickListener(new View.OnClickListener()
        {
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

    public void ShowDialogBox(final PhotoData imageData){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_dialog);

        //Getting custom dialog views
        TextView Image_name = dialog.findViewById(R.id.txt_image_title);
        ImageView imageView = dialog.findViewById(R.id.img);
        EditText descriptionView = dialog.findViewById(R.id.txt_image_desc);
        Button btn_Close = dialog.findViewById(R.id.btn_close);
        Button btn_Save = dialog.findViewById(R.id.btn_save);

        //TODO change to path title - don't show if image is part of path?
        String title = imageData.getPhotoFile();

        //extracting name
        int index = title.indexOf("/");
        String name = title.substring(index+1,title.length());
        Image_name.setText(name);

        descriptionView.setText(imageData.getDescription());

        //TODO use async task?
        imageView.setImageBitmap(BitmapFactory.decodeFile(imageData.getPhotoFile()));

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
                listViewBool = !listViewBool;
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

    //TODO async task for showing images - move into service and use the same one for adaptor and this??
    private static class ShowSingleImageTask extends AsyncTask<FileAndView, Void, Bitmap> {
        FileAndView fileAndView;

        @Override
        protected Bitmap doInBackground(FileAndView... fileAndViews) {
            fileAndView= fileAndViews[0];
            return BitmapFactory.decodeFile(fileAndView.file.getAbsolutePath());
        }
        @Override
        protected void onPostExecute (Bitmap bitmap){
            fileAndView.image.setImageBitmap(bitmap);
        }
    }
    private class FileAndView {
        File file;
        ImageView image;
        public FileAndView(File file, ImageView image) {
            this.file = file;
            this.image = image;
        }
    }

}