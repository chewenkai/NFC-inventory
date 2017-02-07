package com.kevin.rfidmanager.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity {
    PhotoActivity instance = this;
    ImageView imageView;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageView = (ImageView) findViewById(R.id.show_photo_view);
        String filePath = getIntent().getStringExtra(ConstantManager.INTENT_STRING_EXTRA_FILE_PATH);
        imageView.setImageDrawable(Drawable.createFromPath(filePath));
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.update();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mAttacher.cleanup();
            imageView = null;
            mAttacher = null;
            instance = null;
            onDestroy();
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}
