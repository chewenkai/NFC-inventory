package com.kevin.rfidmanager.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.ExitApplication;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity {
    ImageView imageView;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ExitApplication.getInstance().addActivity(this);
        imageView = (ImageView) findViewById(R.id.show_photo_view);
        String filePath = getIntent().getStringExtra(ConstantManager.INTENT_STRING_EXTRA_FILE_PATH);
        imageView.setImageDrawable(Drawable.createFromPath(filePath));
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.update();
    }
}
