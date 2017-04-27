package com.kevin.rfidmanager.Adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.kevin.rfidmanager.R

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */

class ViewPagerFragment : Fragment() {

    private var asset: String? = null

    fun setAsset(asset: String) {
        this.asset = asset
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.view_pager_page, container, false)

        if (savedInstanceState != null) {
            if (asset == null && savedInstanceState.containsKey(BUNDLE_ASSET)) {
                asset = savedInstanceState.getString(BUNDLE_ASSET)
            }
        }
        if (asset != null) {
            val imageView = rootView.findViewById(R.id.imageView) as SubsamplingScaleImageView
            imageView.setImage(ImageSource.uri(asset!!))
        }

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val rootView = view
        if (rootView != null) {
            outState!!.putString(BUNDLE_ASSET, asset)
        }
    }

    companion object {
        private val BUNDLE_ASSET = "asset"
    }

}