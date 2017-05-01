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
import com.kevin.rfidmanager.Activity.ItemInventoryActivity
import com.kevin.rfidmanager.Activity.ItemListActivity
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.SPUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.kevin.rfidmanager.database.ImagesPathDao
import com.kevin.rfidmanager.database.Items
import com.kevin.rfidmanager.database.KeyDescription
import com.kevin.rfidmanager.database.KeyDescriptionDao
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

class ItemListAdaper(val activity: Activity, internal var itemes: MutableList<Items>,
                     var recyclerView: RecyclerView? = null, var deleteItemsButton: CircleButton,
                     internal var emptyHint: TextView, internal val isItemListAdapter: Boolean) : RecyclerView.Adapter<ItemListAdaper.ViewHolder>() {
    val circleDialog: ProgressDialog = ProgressDialog(activity)
    var deleteMdoe = false
    val checkedItems: ArrayList<Items> = ArrayList<Items>()
    val context: Context
        get() = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var contactView = inflater.inflate(R.layout.item_adapter_layout, parent, false)
        // Inflate the custom layout
        if (SPUtil.getInstence(activity).apperance == ConstantManager.DETAIL_LAYOUT)
            contactView = inflater.inflate(R.layout.item_adapter_detail_layout, parent, false)

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
        swipeLayout.isRightSwipeEnabled = false

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        if (isItemListAdapter)
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
                if (File(item.mainImagePath).exists()) {
                    Picasso.with(activity).load(File(item.mainImagePath)).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
                } else {
                    Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
                }
            } else {
                Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
            }
        }

        image.setOnClickListener {
            if (isItemListAdapter)
                (activity as ItemListActivity).currentID = item.rfid
            else
                (activity as ItemInventoryActivity).currentID = item.rfid
            val intent = Intent(activity, ItemDetailActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, item.rfid)
            activity.startActivity(intent)
        }

        if (isItemListAdapter) {
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
        }

        holder.editItem.setOnClickListener {
            if (isItemListAdapter)
                (activity as ItemListActivity).currentID = item.rfid
            else
                (activity as ItemInventoryActivity).currentID = item.rfid
            //                ((MainActivity)activity).viewPager.setCurrentItem(ConstantManager.EDIT, false);
            //                ((MainActivity)activity).adapter.tab3.refreshUI();
            val intent = Intent(activity, ItemEditActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, item.rfid)
            activity.startActivity(intent)
        }
        holder.deleteItem.setOnClickListener { deleteItemDialog(item) }

        val itemName = holder.itemName
        itemName.text = item.itemName

        val keys = DatabaseUtil.queryItemsKeyDes(activity, item.rfid)
        var keyText: StringBuffer = StringBuffer()
        for (key: KeyDescription in keys) {
            keyText.append(" * " + key.keyDescription + "\n")
        }
        holder.keyDes.hint = context.getString(R.string.no_key_description_information)
        holder.keyDes.setText(keyText)
        when (SPUtil.getInstence(activity).apperance) {
            ConstantManager.LINEAR_LAYOUT, ConstantManager.STAGGER_LAYOUT, ConstantManager.ONE_ROW_LAYOUT  // ConstantManager.LINEAR_LAYOUT
            -> {
                holder.keyDes.visibility = View.GONE
            }
            ConstantManager.DETAIL_LAYOUT -> {
                holder.keyDes.visibility = View.VISIBLE
            }
        }
        holder.price.text = "$" + (item.price.toInt()).toString()

    }

    override fun getItemCount(): Int {
        if (itemes.size == 0)
            emptyHint.visibility = View.VISIBLE
        else
            emptyHint.visibility = View.GONE
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
            val daoSession = (activity.application as MyApplication).getmDaoSession()
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

    /**
     * This method used to refresh list data.
     * We need to query all item in var itemes, make sure all of it is exist in database
     */
    fun updateUI() {
        this.itemes.clear()
        this.itemes.addAll(DatabaseUtil.queryItems(activity, (activity as ItemListActivity).currentUser))
        this.notifyDataSetChanged()
        getItemCount()
    }


    /**
     * Set the new Items as data and notify list to refresh.
     */
    fun updateUI(items: ArrayList<Items>) {
//        var itemesIter: MutableIterator<Items> = itemes.iterator();
//        while (itemesIter.hasNext()) {
//            var originItem = itemesIter.next()
//
//            if (!items.contains(originItem)) {
//                val index = itemes.indexOf(originItem)
//                itemesIter.remove()
//                notifyItemRemoved(index)
//            }
//        }
//
//        for (item in items) {
//            if (!itemes.contains(item)) {
//                itemes.add(item)
//                notifyDataSetChanged()
//            }
//        }
        this.itemes.removeAll(this.itemes)
        this.itemes.addAll(items)
        this.notifyDataSetChanged()
        getItemCount()
    }

    /**
     * When card reader find a new card which has been saved in the database.
     * Then add it in the item list.
     */
    fun addNewItemToList(item: Items) {
        if (itemes.contains(item))
            return
        else {
            itemes.add(item)
            this.notifyDataSetChanged()
        }
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
        var keyDes: TextView
        var price: TextView

        init {
            swipeLayout = itemView.findViewById(R.id.swipe_layout) as SwipeLayout
            image = itemView.findViewById(R.id.item_thumb) as ImageView
            itemName = itemView.findViewById(R.id.list_item_name) as TextView
            editItem = itemView.findViewById(R.id.edit_item) as CircleButton
            deleteItem = itemView.findViewById(R.id.remove_item) as CircleButton
            deleteCheckBox = itemView.findViewById(R.id.item_delete_check_box) as CheckBox
            keyDes = itemView.findViewById(R.id.itemlist_key_des) as TextView
            price = itemView.findViewById(R.id.et_price) as TextView
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
            val daoSession = (activity.application as MyApplication).getmDaoSession()
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