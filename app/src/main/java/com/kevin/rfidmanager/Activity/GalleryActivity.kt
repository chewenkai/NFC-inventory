package com.kevin.rfidmanager.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kevin.rfidmanager.Adapter.ViewPagerFragment
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.database.ImagesPath
import kotlinx.android.synthetic.main.activity_gallery.*

/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */

class GalleryActivity : AppCompatActivity() {
    internal var default_position = 0
    private var currentID = ConstantManager.DEFAULT_RFID
    var imagesPaths: List<ImagesPath>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        default_position = intent.getIntExtra(ConstantManager.GALLERY_CLICK_POSITION, 0)
        currentID = intent.getStringExtra(ConstantManager.CURRENT_ITEM_ID)
        imagesPaths = DatabaseUtil.queryImagesPaths(this, currentID)
        var screenSlidePagerAdapter: ScreenSlidePagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pager.adapter = screenSlidePagerAdapter
        pager.currentItem = default_position
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment = ViewPagerFragment()
            fragment.setAsset(imagesPaths!!.get(position).imagePath)
            return fragment
        }

        override fun getCount(): Int {
            return imagesPaths!!.size
        }

    }
}
