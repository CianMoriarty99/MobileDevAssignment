package com.example.assignment112_1;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides static methods to help with handling photos in the app.
 */

// TODO shouldn't this class be merged with BitmapHelper?
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

    public static String getThumbFile(String filePath, int reqWidth, int reqHeight, Context context) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName="I_"+ timeStamp +".jpg";
        try {
            File thumbFile = new File(context.getFilesDir() + File.separator + mImageName);
            FileOutputStream fos = new FileOutputStream(thumbFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return thumbFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
