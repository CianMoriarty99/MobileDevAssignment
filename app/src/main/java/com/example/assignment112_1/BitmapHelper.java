package com.example.assignment112_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapHelper {
    public static File generateThumbnail(String fileString, int reqWidth, int reqHeight, Context context) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions

        File fullFile = new File(fileString);
        String filePath = fullFile.getAbsolutePath();
        Log.d("FILEPATH", filePath);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap finalBit = BitmapFactory.decodeFile(filePath, options);
        File thumbFile = new File(context.getFilesDir(), "fileString");

        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(thumbFile));
            finalBit.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return thumbFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fullFile;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
