package com.kevin.rfidmanager.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;

import java.io.File;
import java.util.List;

/**
 * Created by Kevin on 2017/1/29.
 */

public class GallaryAdaper extends RecyclerView.Adapter<GallaryAdaper.ViewHolder> {
    public Activity activity;
    List<ImagesPath> paths;

    public GallaryAdaper(Activity activity, List<ImagesPath> paths) {
        this.activity = activity;
        this.paths = paths;
    }

    public Context getContext() {
        return activity.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.gallary_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        final ImagesPath path = paths.get(position);

        // Set item views based on your views and data model
        ImageView image = holder.image;
        image.setImageURI(Uri.fromFile(new File(path.getImagePath())));
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nothing
            }
        });
        Button button = holder.removeButton;
        button.setText("Delete");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaoSession daoSession = ((MyApplication) activity.getApplicationContext()).getDaoSession();
                daoSession.getImagesPathDao().delete(path);
                updateUI();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public void updateUI() {
        this.paths.clear();
        this.paths.addAll(DatabaseUtil.queryImagesPaths(activity));
        this.notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView image;
        public Button removeButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.gallary_image);
            removeButton = (Button) itemView.findViewById(R.id.remove_gallary_button);
        }
    }
}
