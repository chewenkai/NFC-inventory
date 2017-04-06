package com.kevin.rfidmanager.Adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kevin.rfidmanager.Entity.DeviceFile
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.USBUtil
import java.util.*

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */


class StorageDevicesAdaper(var activity: Activity, internal var paths: List<DeviceFile>) : RecyclerView.Adapter<StorageDevicesAdaper.ViewHolder>() {
    var selectedDeviceRootPath: DeviceFile? = null
    var radioButtons: MutableList<AppCompatRadioButton> = ArrayList()

    val context: Context
        get() = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.recycle_adapter_storage_devices, parent, false)

        // Return a new holder instance
        val viewHolder = ViewHolder(contactView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val path = paths[position]
        val name = path.deviceName

        // show the device name
        val textViewDeviceName = holder.deviceName
        textViewDeviceName.text = name

        // listen the event of radio button
        val radioButton = holder.radioButton
        if (!radioButtons.contains(radioButton))
            radioButtons.add(radioButton)
        radioButton.setOnClickListener {
            clearAllRadioButtons()
            radioButton.isChecked = true
            selectedDeviceRootPath = path
        }

    }

    override fun getItemCount(): Int {
        return paths.size
    }


    fun updateDataSet() {
        this.paths = USBUtil.getDevicePathSet(activity)
        this.notifyDataSetChanged()
    }

    fun clearAllRadioButtons() {
        for (radioButton in radioButtons) {
            radioButton.isChecked = false
        }
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder// We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    (itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var deviceName: TextView
        var radioButton: AppCompatRadioButton

        init {

            deviceName = itemView.findViewById(R.id.device_name) as TextView
            radioButton = itemView.findViewById(R.id.device_ratio_button) as AppCompatRadioButton
        }// Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
    }

    fun getPermmision() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(activity,
                        Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.MANAGE_DOCUMENTS)) {

                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS),
                        100)

            } else {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS),
                        100)
            }
        } else {
            //Call whatever you want
            //            myMethod();
        }

    }

}