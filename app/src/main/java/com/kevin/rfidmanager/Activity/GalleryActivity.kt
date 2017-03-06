package com.kevin.rfidmanager.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.daimajia.slider.library.Indicators.PagerIndicator
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import java.io.File

class GalleryActivity : AppCompatActivity() {
    internal var default_position = 0
    internal var imageView: SliderLayout? = null
    private var currentID = ConstantManager.DEFAULT_RFID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        imageView = findViewById(R.id.show_photo_view) as SliderLayout
        default_position = intent.getIntExtra(ConstantManager.GALLERY_CLICK_POSITION, 0)
        currentID = intent.getStringExtra(ConstantManager.CURRENT_ITEM_ID)
        val imagesPaths = DatabaseUtil.queryImagesPaths(this, currentID)
        for (imagePath in imagesPaths) {
            val textSliderView = TextSliderView(this)
            textSliderView.image(File(imagePath.imagePath)).scaleType = BaseSliderView.ScaleType.CenterInside
            imageView!!.addSlider(textSliderView)
        }
        imageView!!.stopAutoCycle()
        imageView!!.setCurrentPosition(default_position, true)
        imageView!!.indicatorVisibility = PagerIndicator.IndicatorVisibility.Visible

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
}
