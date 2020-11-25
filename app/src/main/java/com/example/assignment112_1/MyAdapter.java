
package com.example.assignment112_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment112_1.model.PhotoData;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.View_Holder> {
    private Context context;
    private List<PhotoData> items;

    public MyAdapter(List<PhotoData> items) {
        this.items = items;
    }

    public MyAdapter(Context cont, List<PhotoData> items) {
        super();
        this.items = items;
        context = cont;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,
                parent, false);
        View_Holder holder = new View_Holder(v);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final View_Holder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        if (holder != null && items.get(position) != null) {
            if (items.get(position).getThumbFile() != null) {
                File file = new File(items.get(position).getPhotoFile());
                new UploadSingleImageTask().execute(new FileAndHolder(file, holder));
            }
        }
        //animate(holder);
    }


    // convenience method for getting data at click position
    PhotoData getItem(int id) {
        return items.get(id);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class View_Holder extends RecyclerView.ViewHolder {
        ImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }

    public List<PhotoData> getItems() {
        return items;
    }

    public void setItems(List<PhotoData> items) {
        this.items = items;
    }


    private class UploadSingleImageTask extends  AsyncTask<FileAndHolder, Void, Bitmap> {
        FileAndHolder fileAndHolder;

        @Override
        protected Bitmap doInBackground(FileAndHolder... fileAndHolders) {
            fileAndHolder= fileAndHolders[0];
            return BitmapFactory.decodeFile(fileAndHolder.file.getAbsolutePath());
        }
        @Override
        protected void onPostExecute (Bitmap bitmap){
            fileAndHolder.holder.imageView.setImageBitmap(bitmap);
        }
    }

    private class FileAndHolder{
        File file;
        View_Holder holder;
        public FileAndHolder(File file, View_Holder holder) {
            this.file = file;
            this.holder = holder;
        }
    }
}