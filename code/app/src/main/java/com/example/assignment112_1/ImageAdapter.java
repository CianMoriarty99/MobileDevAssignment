
package com.example.assignment112_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment112_1.model.PhotoData;

import java.io.File;
import java.util.List;

/**
 * This class provides the functionality required to display the images taken by the user in a
 * gallery format.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.View_Holder>  {
    private List<PhotoData> images;
    private final ImageListener mImageListener;

    public ImageAdapter(List<PhotoData> images, ImageListener mImageListener) {
        this.images = images;
        this.mImageListener = mImageListener;
    }


    @NonNull
    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,
                    parent, false);
        return new View_Holder(v, mImageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final View_Holder holder, final int position) {
        if (holder != null && images.get(position) != null) {
            File file = new File(images.get(position).getThumbFile());
            new ImageHelper.ShowSingleImageTask().execute(new ImageHelper.FileAndView(file, holder.imageView));
        }
    }


    // convenience method for getting data at click position
    PhotoData getItem(int id) {
        return images.get(id);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public interface ImageListener {
        void onImageClick(int position);
    }

    public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageListener imageListener;


        View_Holder(View itemView, ImageListener imageListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
            itemView.setOnClickListener(this);
            this.imageListener = imageListener;
        }

        @Override
        public void onClick(View view) {
            imageListener.onImageClick(getAdapterPosition());
        }
    }

    public List<PhotoData> getImages() {
        return images;
    }

    public void setImages(List<PhotoData> images) {
        this.images = images;
    }

}