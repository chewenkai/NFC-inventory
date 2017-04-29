package com.kevin.rfidmanager.Adapter

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import at.markushi.ui.CircleButton
import com.kevin.rfidmanager.Activity.ItemDetailActivity
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.SPUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.kevin.rfidmanager.database.Items
import com.kevin.rfidmanager.database.KeyDescription
import com.squareup.picasso.Picasso
import org.jetbrains.anko.toast
import java.io.File

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */
class CheckoutAdaper(val activity: Activity, var recyclerView: RecyclerView? = null,
                     var deleteItemsButton: CircleButton, var tv_checkout_result: TextView) : RecyclerView.Adapter<CheckoutAdaper.ViewHolder>() {
    var detectedItems = ArrayList<ItemWithCount>()
    val backupDetectedItems = ArrayList<ItemWithCount>()
    val circleDialog: ProgressDialog = ProgressDialog(activity)
    var deleteMdoe = false
    val checkedItems: MutableList<ItemWithCount> = ArrayList<ItemWithCount>()
    val context: Context
        get() = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.checkout_adapter_layout, parent, false)

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

        // Get the data model based on position
        val item = detectedItems[position]

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
        if (item.item!!.mainImagePath == null) {
            Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(activity).load(File(item.item!!.mainImagePath)).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
            } else {
                Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity), 0).into(image)
            }
        }

        image.setOnClickListener {
            val intent = Intent(activity, ItemDetailActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, item.item!!.rfid)
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

        holder.count.text = item.count.toString()

        //  Add one
        holder.add.setOnClickListener {
            // if avaliable inventory > 0, then can add item
            if (item.item?.avaliableInventory!! <= 0) {
                activity.toast("Out of stock.")
                return@setOnClickListener
            }
            // deduct one from database
            item.item?.avaliableInventory = item.item?.avaliableInventory!! - 1

            item.countIncrease()
            holder.count.text = item.count.toString()
            holder.price.text = "$" + (item.item?.price?.toInt()).toString() + "\nremain:" + item?.item?.avaliableInventory.toString()
            refreshPriceTextView()
        }

        //  Deduct one
        holder.deduct.setOnClickListener {
            if (item.count <= 0)
                return@setOnClickListener
            else {
                item.item?.avaliableInventory = item.item?.avaliableInventory!! + 1
            }

            item.countDecrease()
            holder.count.text = item.count.toString()
            holder.price.text = "$" + (item.item?.price?.toInt()).toString() + "\nremain:" + item?.item?.avaliableInventory.toString()
            refreshPriceTextView()
        }

        val itemName = holder.itemName
        itemName.text = item.item!!.itemName

        holder.price.text = "$" + (item.item?.price?.toInt()).toString() + "\nremain:" + item?.item?.avaliableInventory.toString()

        val keys = DatabaseUtil.queryItemsKeyDes(activity, item.item!!.rfid)
        var keyText: StringBuffer = StringBuffer()
        for (key: KeyDescription in keys) {
            keyText.append(" * " + key.keyDescription + "\n")
        }

        holder.keyDes.text = keyText
        when (SPUtil.getInstence(activity).apperance) {
            8  // ConstantManager.LINEAR_LAYOUT
            -> {
                holder.keyDes.visibility = View.GONE
            }
            9  // ConstantManager.STAGGER_LAYOUT
            -> {
                holder.keyDes.visibility = View.GONE
            }
            10  // ConstantManager.ONE_ROW_LAYOUT
            -> {
                holder.keyDes.visibility = View.GONE
            }
            11 -> {
                holder.keyDes.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return detectedItems!!.size
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
        checkedItems!!.removeAll(checkedItems)
    }

    fun addItem(item: ItemWithCount) {
        var isContain = false
        for (item_i: ItemWithCount in detectedItems) {
            if (item_i.item!!.rfid == item.item!!.rfid) {
                isContain = true
                break
            }
        }
        if (!isContain) {
            if (item.item?.avaliableInventory == 0)
                item.count = 0
            else {
                item.count = 1
                item.item?.avaliableInventory = item.item?.avaliableInventory!! - 1
            }
            detectedItems.add(item)
            backupDetectedItems.add(item)
            updateUI()
        }
    }

    /**
     * Count the money by items
     */
    fun countTotalMoney(): Float {
        var result = 0f
        for (item: ItemWithCount in detectedItems!!) {
            result += item.item!!.price * item.count
        }
        return result
    }

    fun refreshPriceTextView() {
        tv_checkout_result.text = countTotalMoney().toString()
    }

    fun updateUI() {
        this.notifyDataSetChanged()
        refreshPriceTextView()
    }

    fun deleteSelectedItems() {
        circleDialog.setTitle("Deleting...")
        circleDialog.setMessage("please wait a while")
        circleDialog.show()
        Thread().run {
            for (i: ItemWithCount in checkedItems!!) {
                detectedItems!!.remove(i)
            }
            circleDialog.dismiss()
            deleteItemsButton.visibility = View.GONE
            deleteMdoe = false
            updateUI()
            refreshPriceTextView()
            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show()
        }

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var image: ImageView
        var itemName: TextView
        var price: TextView
        var add: CircleButton
        var count: TextView
        var deduct: CircleButton
        var deleteCheckBox: CheckBox
        var keyDes: TextView

        init {
            image = itemView.findViewById(R.id.item_thumb) as ImageView
            itemName = itemView.findViewById(R.id.list_item_name) as TextView
            price = itemView.findViewById(R.id.price) as TextView
            add = itemView.findViewById(R.id.edit_item) as CircleButton
            count = itemView.findViewById(R.id.item_count) as TextView
            deduct = itemView.findViewById(R.id.remove_item) as CircleButton
            deleteCheckBox = itemView.findViewById(R.id.item_delete_check_box) as CheckBox
            keyDes = itemView.findViewById(R.id.itemlist_key_des) as TextView
        }// Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
    }


    class ItemWithCount {
        var item: Items? = null
        var count: Int = 1
        fun countIncrease() {
            count++
        }

        fun countDecrease() {
            if (count >= 1)
                count--
        }
    }
}