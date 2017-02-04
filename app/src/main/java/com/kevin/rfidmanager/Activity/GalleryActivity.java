package com.kevin.rfidmanager.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ExitApplication;
import com.kevin.rfidmanager.database.ImagesPath;

import java.io.File;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    int default_position = 0;
    SliderLayout imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ExitApplication.getInstance().addActivity(this);
        imageView = (SliderLayout) findViewById(R.id.show_photo_view);
        default_position = getIntent().getIntExtra(ConstantManager.GALLERY_CLICK_POSITION, 0);
        List<ImagesPath> imagesPaths = DatabaseUtil.queryImagesPaths(this);
        for (ImagesPath imagePath:imagesPaths) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(new File(imagePath.getImagePath())).setScaleType(BaseSliderView.ScaleType.CenterInside);
            imageView.addSlider(textSliderView);
        }
        imageView.stopAutoCycle();
        imageView.setCurrentPosition(default_position, true);
        imageView.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
    }
}
