package com.example.assignment112_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

public class ImageHelper {
    public static class ShowSingleImageTask extends AsyncTask<FileAndView, Void, Bitmap> {
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
    public static class FileAndView {
        File file;
        ImageView image;
        public FileAndView(File file, ImageView image) {
            this.file = file;
            this.image = image;
        }
    }
}
