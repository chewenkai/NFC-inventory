package com.kevin.rfidmanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kevin.rfidmanager.Bean.SaleStasticInfo
import com.kevin.rfidmanager.R

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */
class StatisticListAdapter(context: Context, var saleInfos: ArrayList<SaleStasticInfo>) : ArrayAdapter<SaleStasticInfo>(context, R.layout.statistic_adaper_layout, saleInfos) {

    // View lookup cache
    private class ViewHolder {
        internal var itemName: TextView? = null
        internal var unitPrice: TextView? = null
        internal var volume: TextView? = null
        internal var price: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val saleInfo = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.statistic_adaper_layout, parent, false)
            viewHolder.itemName = convertView!!.findViewById(R.id.item_name) as TextView
            viewHolder.unitPrice = convertView.findViewById(R.id.unit_price) as TextView
            viewHolder.volume = convertView.findViewById(R.id.volume) as TextView
            viewHolder.price = convertView.findViewById(R.id.price) as TextView
            // Cache the viewHolder object inside the fresh view
            convertView.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = convertView.tag as ViewHolder
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        viewHolder.itemName?.text = saleInfo.itemName
        viewHolder.unitPrice?.text = saleInfo.price.toString()
        viewHolder.volume?.text = saleInfo.volume.toString()
        viewHolder.price?.text = (saleInfo.price * saleInfo.volume).toString()
        return convertView
    }

    fun updateDate(statisticResult: List<SaleStasticInfo>) {
        saleInfos.clear()
        saleInfos.addAll(statisticResult)
        notifyDataSetChanged()
    }
}