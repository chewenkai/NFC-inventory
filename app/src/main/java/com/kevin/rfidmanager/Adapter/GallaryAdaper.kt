package com.kevin.rfidmanager.Adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import at.markushi.ui.CircleButton
import com.kevin.rfidmanager.Activity.GalleryActivity
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.ConstantManager.DEFAULT_IMAGE_HEIGHT_DP
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.kevin.rfidmanager.database.ImagesPath
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

class GallaryAdaper(var activity: Activity, private val paths: MutableList<ImagesPath>, private val hide: Boolean, currentID: String) : RecyclerView.Adapter<GallaryAdaper.ViewHolder>() {
    private var currentID = ConstantManager.DEFAULT_RFID

    init {
        this.currentID = currentID
    }

    val context: Context
        get() = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.gallary_layout, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val path = paths[position]

        // Set item views based on your views and data model
        val image = holder.image
        image.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    // Wait until layout to call Picasso
                    override fun onGlobalLayout() {
                        // Ensure we call this only once
                        image.viewTreeObserver
                                .removeOnGlobalLayoutListener(this)
                    }
                })
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Picasso.with(activity).load(File(path.imagePath)).resize(0,
                    ScreenUtil.dpToPx(activity, DEFAULT_IMAGE_HEIGHT_DP)).into(image)
        } else {
            Picasso.with(activity).load(R.drawable.image_read_fail).resizeDimen(0,
                    ScreenUtil.dpToPx(activity, DEFAULT_IMAGE_HEIGHT_DP)).into(image)
        }
        image.setOnClickListener {
            val intent = Intent()
            intent.putExtra(ConstantManager.GALLERY_CLICK_POSITION, holder.adapterPosition)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, currentID)
            intent.setClass(activity, GalleryActivity::class.java)
            activity.startActivity(intent)
        }
        val button = holder.removeButton
        if (hide) {
            button.visibility = View.GONE
        }
        button.setOnClickListener {
            val daoSession = (activity.applicationContext as MyApplication).getDaoSession()
            daoSession.imagesPathDao.delete(path)
            updateUI()
        }
    }

    override fun getItemCount(): Int {
        return paths.size
    }

    fun updateUI() {
        this.paths.clear()
        this.paths.addAll(DatabaseUtil.queryImagesPaths(activity, currentID))
        this.notifyDataSetChanged()
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder// We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    (itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var image: ImageView
        var removeButton: CircleButton

        init {

            image = itemView.findViewById(R.id.gallary_image) as ImageView
            removeButton = itemView.findViewById(R.id.remove_gallary_button) as CircleButton
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
