package com.example.assignment112_1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.assignment112_1.model.PhotoData;

public class FullView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);


        ImageView imageView = findViewById(R.id.img_full);

        PhotoData img = getIntent().getExtras().getParcelable("img");
        imageView.setImageBitmap(BitmapFactory.decodeFile(img.getPhotoFile()));


    }
}