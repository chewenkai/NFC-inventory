package com.kevin.rfidmanager.Adapter

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import at.markushi.ui.CircleButton
import com.daimajia.swipe.SwipeLayout
import com.kevin.rfidmanager.Activity.ItemDetailActivity
import com.kevin.rfidmanager.Activity.ItemEditActivity
import com.kevin.rfidmanager.Activity.ItemListActivity
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.kevin.rfidmanager.database.ImagesPathDao
import com.kevin.rfidmanager.database.Items
import com.kevin.rfidmanager.database.KeyDescriptionDao
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

class ItemListAdaper(val activity: Activity, internal var itemes: MutableList<Items>,
                     var recyclerView: RecyclerView? = null, var deleteItemsButton: CircleButton) : RecyclerView.Adapter<ItemListAdaper.ViewHolder>() {
    val circleDialog: ProgressDialog = ProgressDialog(activity)
    var deleteMdoe = false
    val checkedItems: ArrayList<Items> = ArrayList<Items>()
    val context: Context
        get() = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        var contactView = inflater.inflate(R.layout.item_adapter_layout, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theCheckBox: CheckBox = holder.deleteCheckBox
        if (deleteMdoe) {
            theCheckBox.visibility = View.VISIBLE
            deleteItemsButton.visibility = View.VISIBLE
        } else {
            theCheckBox.visibility = View.GONE
            deleteItemsButton.visibility = View.GONE
        }

        val swipeLayout = holder.swipeLayout
        //set show mode.
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.itemView.findViewById(R.id.bottom_wrapper))

        // Get the data model based on position
        val item = itemes[position]

        theCheckBox.setOnClickListener {
            if (!theCheckBox.isChecked and (item in checkedItems)) {
                checkedItems.remove(item)
            }
            if (theCheckBox.isChecked and !(item in checkedItems)) {
                checkedItems.add(item)
            }
        }

        // Set item views based on your views and data model
        val image = holder.image
        if (item.mainImagePath == null) {
            Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(activity).load(File(item.mainImagePath)).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
            } else {
                Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
            }
        }

        image.setOnClickListener {
            (activity as ItemListActivity).currentID = item.rfid
            val intent = Intent(activity, ItemDetailActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, item.rfid)
            activity.startActivity(intent)
        }
        val longClickListener = { v: View ->
            deleteMdoe = !deleteMdoe
            if (deleteMdoe) {
                setCachedCheckBoxVisible()
                theCheckBox.isChecked = true
                checkedItems.add(item)
                deleteItemsButton.visibility = View.VISIBLE
            } else {
                setCachedCheckBoxGone()
                deleteItemsButton.visibility = View.GONE
            }
            true
        }
        image.setOnLongClickListener(longClickListener)

        holder.editItem.setOnClickListener {
            (activity as ItemListActivity).currentID = item.rfid
            //                ((MainActivity)activity).viewPager.setCurrentItem(ConstantManager.EDIT, false);
            //                ((MainActivity)activity).adapter.tab3.refreshUI();
            val intent = Intent(activity, ItemEditActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, activity.currentID)
            activity.startActivity(intent)
        }
        holder.deleteItem.setOnClickListener { deleteItemDialog(item) }

        val itemName = holder.itemName
        itemName.text = item.itemName
    }

    override fun getItemCount(): Int {
        return itemes.size
    }

    fun setCachedCheckBoxVisible() {
        for (i: Int in 0..this.itemCount - 1) {
            val defaultViewHolder = this.recyclerView!!.findViewHolderForAdapterPosition(i)
            if (defaultViewHolder != null) {
                (defaultViewHolder as ViewHolder).deleteCheckBox.visibility = View.VISIBLE
            }
        }
        deleteItemsButton.visibility = View.VISIBLE
    }

    fun setCachedCheckBoxGone() {
        for (i: Int in 0..this.itemCount - 1) {
            val defaultViewHolder = this.recyclerView!!.findViewHolderForAdapterPosition(i)
            if (defaultViewHolder != null) {
                (defaultViewHolder as ViewHolder).deleteCheckBox.visibility = View.GONE
                (defaultViewHolder as ViewHolder).deleteCheckBox.isChecked = false
            }
        }
        deleteItemsButton.visibility = View.GONE
        checkedItems.removeAll(checkedItems)
    }

    fun deleteSelectedItems() {
        circleDialog.setTitle("Deleting...")
        circleDialog.setMessage("please wait a while")
        circleDialog.show()
        Thread().run {
            val daoSession = (activity.application as MyApplication).getDaoSession()
            for (i: Int in 0..checkedItems.size - 1) {
                daoSession.itemsDao.deleteInTx(checkedItems.get(i))
                // delete image path
                daoSession.imagesPathDao.deleteInTx(daoSession.imagesPathDao.queryBuilder().where(ImagesPathDao.Properties.Rfid.eq(checkedItems.get(i).rfid)).build().list())
                // delete key description
                daoSession.keyDescriptionDao.deleteInTx(daoSession.keyDescriptionDao.queryBuilder().where(KeyDescriptionDao.Properties.Rfid.eq(checkedItems.get(i).rfid)).build().list())
            }
            circleDialog.dismiss()
            deleteItemsButton.visibility = View.GONE
            deleteMdoe = false
            updateUI()
            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show()
        }

    }

    fun updateUI() {
        this.itemes.clear()
        this.itemes.addAll(DatabaseUtil.queryItems(activity, (activity as ItemListActivity).currentUser))
        this.notifyDataSetChanged()
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var swipeLayout: SwipeLayout
        var image: ImageView
        var itemName: TextView
        var editItem: CircleButton
        var deleteItem: CircleButton
        var deleteCheckBox: CheckBox

        init {
            swipeLayout = itemView.findViewById(R.id.swipe_layout) as SwipeLayout
            image = itemView.findViewById(R.id.item_thumb) as ImageView
            itemName = itemView.findViewById(R.id.list_item_name) as TextView
            editItem = itemView.findViewById(R.id.edit_item) as CircleButton
            deleteItem = itemView.findViewById(R.id.remove_item) as CircleButton
            deleteCheckBox = itemView.findViewById(R.id.item_delete_check_box) as CheckBox
        }// Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
    }

    /*
    This is a dialog used for delete item
     */
    fun deleteItemDialog(item: Items) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.delete_confirm_title)
        builder.setMessage(R.string.delete_confirm)
        builder.setPositiveButton(R.string.OK) { dialog, which ->
            val daoSession = (activity.application as MyApplication).getDaoSession()
            daoSession.itemsDao.delete(item)
            // delete image path
            daoSession.imagesPathDao.deleteInTx(daoSession.imagesPathDao.queryBuilder().where(ImagesPathDao.Properties.Rfid.eq(item.rfid)).build().list())
            // delete key description
            daoSession.keyDescriptionDao.deleteInTx(daoSession.keyDescriptionDao.queryBuilder().where(KeyDescriptionDao.Properties.Rfid.eq(item.rfid)).build().list())
            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show()
            updateUI()
        }
        builder.setNegativeButton(R.string.Cancel) { dialog, which -> }
        builder.create().show()
    }
}
