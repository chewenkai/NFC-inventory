package com.kevin.rfidmanager.Fragments;

/**
 * Created by Kevin on 2017/1/26.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.rfidmanager.Activity.MainActivity;
import com.kevin.rfidmanager.Activity.PhotoActivity;
import com.kevin.rfidmanager.Adapter.GallaryAdaper;
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.BitMapUtil;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.IntentUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

import at.markushi.ui.CircleButton;


public class ItemDetailFrag extends android.support.v4.app.Fragment {
    private TextView textViewItemName, addKeyDes, detailDescriptionTitle;
    private ListView key_des_list;
    private ImageView mainImage;
    private AppCompatButton addGalleryButton;
    private EditText detailDescription, itemName;

    private RecyclerView recyclerView;
    private GallaryAdaper gallaryAdaper;
    private KeyDesListAdapter desListAdapter;

    private CircleButton saveButton;

    public View view;

    private boolean hideEditButtons = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.item_add_layout, container, false);
        this.view = v;
        initUI(v);
        return v;
    }


    private void initUI(View v) {
        if (getActivity() == null)
            return;

        if (((MyApplication) getActivity().getApplication()).getCurrentItemID() ==
                ConstantManager.DEFAULT_RFID)
            return;

        itemName = (EditText) v.findViewById(R.id.item_name);
        itemName.setVisibility(View.GONE);

        textViewItemName = (TextView) v.findViewById(R.id.textview_item_name);
        textViewItemName.setText(DatabaseUtil.getCurrentItem(getActivity()).getItemName());
        textViewItemName.setVisibility(View.VISIBLE);

        key_des_list = (ListView) v.findViewById(R.id.listview_item_key_des);
        desListAdapter = new KeyDesListAdapter(getActivity(),
                DatabaseUtil.queryItemsKeyDes(getActivity(),
                        ((MyApplication) getActivity().getApplication()).getCurrentItemID()), hideEditButtons);
        key_des_list.setAdapter(desListAdapter);
        desListAdapter.setCurrentActivity(getActivity());

        mainImage = (ImageView) v.findViewById(R.id.iamgeview_main_image);
        final String mainImagePath = DatabaseUtil.getCurrentItem(getActivity()).getMainImagePath();
        if (mainImagePath != null){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(getActivity()).load(new File(mainImagePath)).resize(ScreenUtil.getScreenWidth(getActivity())/2,0).into(mainImage);
                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open picture
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(FileProvider.getUriForFile(getActivity(),
                                getActivity().getApplicationContext().getPackageName() + ".provider", new File(mainImagePath)), "image/*");
                        startActivity(intent);
                    }
                });
            } else {
                Picasso.with(getActivity()).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(getActivity())/2,0).into(mainImage);
                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open picture
                    }
                });
            }
        }else {
            Picasso.with(getActivity()).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(getActivity())/2,0).into(mainImage);
            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open picture
                }
            });
        }


        addKeyDes = (TextView) v.findViewById(R.id.button_add_item_key_des);
        addKeyDes.setVisibility(View.GONE);

        detailDescriptionTitle = (TextView) v.findViewById(R.id.detail_description_title);
        detailDescriptionTitle.setVisibility(View.GONE);

        addGalleryButton = (AppCompatButton) v.findViewById(R.id.add_gallery_image);
        addGalleryButton.setVisibility(View.GONE);

        detailDescription = (EditText) v.findViewById(R.id.detail_description);
        detailDescription.setText(DatabaseUtil.getCurrentItem(getActivity()).getDetailDescription());
        detailDescription.setEnabled(false);
        detailDescription.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        detailDescription.setTextColor(getActivity().getResources().getColor(R.color.black));

        saveButton = (CircleButton) v.findViewById(R.id.save_des);
        saveButton.setVisibility(View.GONE);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycle_gallery);
        gallaryAdaper = new GallaryAdaper(getActivity(), DatabaseUtil.queryImagesPaths(getActivity()), hideEditButtons);
        recyclerView.setAdapter(gallaryAdaper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);// Optionally customize the position you want to default scroll to
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
//        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView.setHasFixedSize(true);

    }

    public void refreshUI(){
        if (getActivity() == null){
            return;
        }

        if (((MyApplication) getActivity().getApplication()).getCurrentItemID() ==
                ConstantManager.DEFAULT_RFID)
            return;

        if (view!=null){
            initUI(view);
        }

    }

}
