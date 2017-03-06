package com.kevin.rfidmanager.Activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.ImageView

import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager


class PhotoActivity : AppCompatActivity() {
    internal var instance: PhotoActivity? = this
    internal var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        imageView = findViewById(R.id.show_photo_view) as ImageView
        val filePath = intent.getStringExtra(ConstantManager.INTENT_STRING_EXTRA_FILE_PATH)
        imageView!!.setImageDrawable(Drawable.createFromPath(filePath))
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            instance = null
            onDestroy()
            finish()
        }
        return super.onKeyUp(keyCode, event)
    }
}
