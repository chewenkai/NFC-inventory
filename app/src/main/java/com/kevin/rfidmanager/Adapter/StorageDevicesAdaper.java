package com.kevin.rfidmanager.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.rfidmanager.Activity.GalleryActivity;
import com.kevin.rfidmanager.Entity.DeviceFile;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import at.markushi.ui.CircleButton;

import static com.kevin.rfidmanager.Utils.ConstantManager.DEFAULT_IMAGE_HEIGHT_DP;

/**
 * Created by Kevin on 2017/1/29.
 */

public class StorageDevicesAdaper extends RecyclerView.Adapter<StorageDevicesAdaper.ViewHolder> {
    public Activity activity;
    List<DeviceFile> paths;
    public DeviceFile selectedDeviceRootPath=null;
    public List<AppCompatRadioButton> radioButtons = new ArrayList<>();

    public StorageDevicesAdaper(Activity activity, List<DeviceFile> paths) {
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
        View contactView = inflater.inflate(R.layout.recycle_adapter_storage_devices, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Get the data model based on position
        final DeviceFile path = paths.get(position);
        final String name = path.deviceName;

        // show the device name
        final TextView textViewDeviceName = holder.deviceName;
        textViewDeviceName.setText(name);

        // listen the event of radio button
        final AppCompatRadioButton radioButton = holder.radioButton;
        if (!radioButtons.contains(radioButton))
            radioButtons.add(radioButton);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllRadioButtons();
                radioButton.setChecked(true);
                selectedDeviceRootPath = path;
            }
        });

    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public void updateUI() {
        this.notifyDataSetChanged();
    }

    public void clearAllRadioButtons(){
        for (AppCompatRadioButton radioButton :
                radioButtons) {
            radioButton.setChecked(false);
        }
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView deviceName;
        public AppCompatRadioButton radioButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            radioButton = (AppCompatRadioButton) itemView.findViewById(R.id.device_ratio_button);
        }
    }

    public void getPermmision() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(activity,
                        Manifest.permission.MANAGE_DOCUMENTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (activity, Manifest.permission.MANAGE_DOCUMENTS)) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                        100);

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                        100);
            }
        } else {
            //Call whatever you want
//            myMethod();
        }

    }

}
