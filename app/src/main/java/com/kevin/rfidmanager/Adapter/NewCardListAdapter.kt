package com.kevin.rfidmanager.Adapter

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatRadioButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kevin.rfidmanager.R

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */
class NewCardListAdapter(context: Context, newCardIDs: MutableList<String>, etID: TextInputEditText) : ArrayAdapter<String>(context, R.layout.recycle_adapter_new_cards, newCardIDs) {
    var radioButtons: MutableList<AppCompatRadioButton> = ArrayList()
    val etID: TextInputEditText
    var newCardIDs: MutableList<String>

    init {
        this.etID = etID
        this.newCardIDs = newCardIDs
    }

    // View lookup cache
    private class ViewHolder {
        internal var cardID: TextView? = null
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
            viewHolder.cardID = convertView!!.findViewById(R.id.card_id) as TextView
            viewHolder.radioButton = convertView.findViewById(R.id.new_card_ratio_button) as AppCompatRadioButton
            // Cache the viewHolder object inside the fresh view
            convertView.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = convertView.tag as ViewHolder
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.cardID!!.text = cardID

        // listen the event of radio button
        val radioButton = viewHolder.radioButton
        if (!radioButtons.contains(radioButton))
            radioButtons.add(radioButton!!)
        radioButton!!.setOnClickListener {
            clearAllRadioButtons()
            radioButton.isChecked = true
            etID.setText(cardID)
        }

        // Return the completed view to render on screen
        return convertView
    }

    fun clearAllRadioButtons() {
        for (radioButton in radioButtons) {
            radioButton.isChecked = false
        }
    }

    fun updateList(cardIDs: ArrayList<String>) {
        newCardIDs.clear()
        newCardIDs.addAll(cardIDs)
        // Clear radio button and ID edittext when add or remove tags
        clearAllRadioButtons()
        etID.setText("")
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