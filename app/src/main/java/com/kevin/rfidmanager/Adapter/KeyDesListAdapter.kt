package com.kevin.rfidmanager.Adapter

import android.app.Activity
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import at.markushi.ui.CircleButton
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.database.KeyDescription

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

class KeyDesListAdapter(context: Context, item_key_des: List<KeyDescription>, hideButton: Boolean = false, currentID: String = ConstantManager.DEFAULT_RFID) : ArrayAdapter<KeyDescription>(context, R.layout.key_description_listview_llayout, item_key_des) {
    private val instance = this
    private var currentActivity: Activity? = null
    private val hideButton: Boolean
    private val currentID: String

    // View lookup cache
    private class ViewHolder {
        internal var keyDescription: TextView? = null
        internal var edit: CircleButton? = null
        internal var delete: CircleButton? = null
    }

    init {
        this.hideButton = hideButton
        this.currentID = currentID
    }

    fun setCurrentActivity(activity: Activity) {
        this.currentActivity = activity
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val keyDescription = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.key_description_listview_llayout, parent, false)
            viewHolder.keyDescription = convertView!!.findViewById(R.id.text_key_des) as TextView
            viewHolder.edit = convertView.findViewById(R.id.layout_edit_key_des) as CircleButton
            viewHolder.delete = convertView.findViewById(R.id.layout_remove_key_des) as CircleButton
            // Cache the viewHolder object inside the fresh view
            convertView.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = convertView.tag as ViewHolder
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.keyDescription!!.text = keyDescription!!.keyDescription
        if (hideButton!!) {
            viewHolder.edit!!.visibility = View.GONE
            viewHolder.delete!!.visibility = View.GONE
        } else {
            viewHolder.edit!!.setOnClickListener { showEditDialog(keyDescription) }
            viewHolder.delete!!.setOnClickListener {
                deleteItemsKeyDes(keyDescription)
                updateKeyDescriptionList()
            }
        }

        // Return the completed view to render on screen
        return convertView
    }

    /*
    This is a dialog used for edit key description
     */
    fun showEditDialog(keyDescription: KeyDescription) {
        val dialogBuilder = AlertDialog.Builder(this.currentActivity!!)
        val inflater = this.currentActivity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_layout_edit_key_des, null)
        dialogBuilder.setView(dialogView)

        val newKeyDes = dialogView.findViewById(R.id.edit_key_des_text_editor) as TextInputEditText
        // The original key description should be shown in the text box when editing key description.
        newKeyDes.setText(keyDescription.keyDescription)

        val saveButton = dialogView.findViewById(R.id.dialog_change) as Button
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as Button

        dialogBuilder.setTitle(currentActivity!!.resources.getString(R.string.dialog_title_edit_key_des))
        val b = dialogBuilder.create()
        b.show()

        saveButton.setOnClickListener {
            editItemsKeyDes(keyDescription, newKeyDes.text.toString())
            updateKeyDescriptionList()
            b.dismiss()
        }

        cancleButton.setOnClickListener { b.dismiss() }
    }

    /**
     * Update the Key Description list
     */
    fun updateKeyDescriptionList() {
        this.clear()
        this.addAll(DatabaseUtil.queryItemsKeyDes(currentActivity, currentID))
        this.notifyDataSetChanged()
    }

    /**
     * delete the items key description in database.
     */
    fun deleteItemsKeyDes(keyDescription: KeyDescription) {
        // get the key description DAO
        val daoSession = (currentActivity!!.application as MyApplication).getmDaoSession()
        val keyDescriptionDao = daoSession.keyDescriptionDao

        keyDescriptionDao.delete(keyDescription)
    }

    /**
     * Update the key description
     * @param keyDescription
     * *
     * @param newKeyDes
     */
    fun editItemsKeyDes(keyDescription: KeyDescription, newKeyDes: String) {
        keyDescription.keyDescription = newKeyDes
        // get the key description DAO
        val daoSession = (currentActivity!!.application as MyApplication).getmDaoSession()
        val keyDescriptionDao = daoSession.keyDescriptionDao
        keyDescriptionDao.insertOrReplace(keyDescription)
        updateKeyDescriptionList()
    }

}