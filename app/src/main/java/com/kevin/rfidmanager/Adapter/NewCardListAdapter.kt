package com.kevin.rfidmanager.Adapter

import android.content.Context
import android.support.v7.widget.AppCompatRadioButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kevin.rfidmanager.R

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */
class NewCardListAdapter(context: Context, newCardIDs: MutableList<String>) : ArrayAdapter<String>(context, R.layout.recycle_adapter_new_cards, newCardIDs) {
    var radioButtons = ArrayList<AppCompatRadioButton>()
    var newCardIDs: MutableList<String>
    var selectedTagId = ""  // used to save the selected status
    init {
        this.newCardIDs = newCardIDs
    }

    // View lookup cache
    private class ViewHolder {
        internal var radioButton: AppCompatRadioButton? = null
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val cardID: String = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.recycle_adapter_new_cards, parent, false)
            viewHolder.radioButton = convertView.findViewById(R.id.new_card_ratio_button) as AppCompatRadioButton
            // Cache the viewHolder object inside the fresh view
            convertView.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = convertView.tag as ViewHolder
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        // listen the event of radio button
        val radioButton = viewHolder.radioButton

        // set text
        radioButton?.text = cardID

        radioButton?.isChecked = radioButton?.text.toString() == selectedTagId

        radioButtons.add(radioButton!!)

        radioButton.setOnClickListener {
            clearAllRadioButtons()
            radioButton.isChecked = true
            selectedTagId = radioButton.text.toString()
        }

        // Return the completed view to render on screen
        return convertView!!
    }

    fun clearAllRadioButtons() {
        for (radioButton in radioButtons) {
            if (radioButton != null)
                radioButton.isChecked = false
        }
    }

    fun updateList(cardIDs: ArrayList<String>) {
        radioButtons.clear()
        newCardIDs.clear()
        newCardIDs.addAll(cardIDs)
        // Clear radio button and ID edittext when add or remove tags
        this.notifyDataSetChanged()
    }

    fun addNewCardToList(cardID: String) {
        if (newCardIDs.contains(cardID))
            return
        else {
            newCardIDs.add(cardID)
            this.notifyDataSetChanged()
        }
    }
}