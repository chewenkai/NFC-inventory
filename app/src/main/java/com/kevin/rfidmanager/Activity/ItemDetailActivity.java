package com.kevin.rfidmanager.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.rfidmanager.Adapter.GallaryAdaper;
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ItemDetailActivity extends AppCompatActivity {
    private TextView textViewItemName, addKeyDes, detailDescriptionTitle;
    private ListView key_des_list;
    private ImageView mainImage;
    private AppCompatButton addGalleryButton;
    private EditText detailDescription, itemName;

    private RecyclerView recyclerView;
    private GallaryAdaper gallaryAdaper;
    private KeyDesListAdapter desListAdapter;
    public String currentID = ConstantManager.DEFAULT_RFID;

    private boolean hideEditButtons = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_layout);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle(R.string.detail_page);
        actionBar.setHomeButtonEnabled(true);
        initUI();
    }

    @Override
    protected void onResume() {
        initUI();
        super.onResume();
    }

    private void initUI() {
        currentID = getIntent().getStringExtra(ConstantManager.CURRENT_ITEM_ID);

        if (currentID == ConstantManager.DEFAULT_RFID)
            return;

        itemName = (EditText) findViewById(R.id.item_name);
        itemName.setVisibility(View.GONE);

        textViewItemName = (TextView) findViewById(R.id.textview_item_name);
        textViewItemName.setText(DatabaseUtil.getCurrentItem(ItemDetailActivity.this, currentID).
                getItemName());
        textViewItemName.setVisibility(View.VISIBLE);

        key_des_list = (ListView) findViewById(R.id.listview_item_key_des);
        desListAdapter = new KeyDesListAdapter(ItemDetailActivity.this,
                DatabaseUtil.queryItemsKeyDes(ItemDetailActivity.this, currentID), hideEditButtons,
                currentID);
        key_des_list.setAdapter(desListAdapter);
        desListAdapter.setCurrentActivity(ItemDetailActivity.this);

        mainImage = (ImageView) findViewById(R.id.iamgeview_main_image);
        final String mainImagePath = DatabaseUtil.getCurrentItem(ItemDetailActivity.this, currentID).getMainImagePath();
        if (mainImagePath != null){
            if (ContextCompat.checkSelfPermission(ItemDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(ItemDetailActivity.this).load(new File(mainImagePath)).resize(ScreenUtil.getScreenWidth(ItemDetailActivity.this)/2,0).into(mainImage);
                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open picture
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(FileProvider.getUriForFile(ItemDetailActivity.this,
                                getApplicationContext().getPackageName() + ".provider", new File(mainImagePath)), "image/*");
                        startActivity(intent);
                    }
                });
            } else {
                Picasso.with(ItemDetailActivity.this).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(ItemDetailActivity.this)/2,0).into(mainImage);
                mainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open picture
                    }
                });
            }
        }else {
            Picasso.with(ItemDetailActivity.this).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(ItemDetailActivity.this)/2,0).into(mainImage);
            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open picture
                }
            });
        }


        addKeyDes = (TextView) findViewById(R.id.button_add_item_key_des);
        addKeyDes.setVisibility(View.GONE);

        detailDescriptionTitle = (TextView) findViewById(R.id.detail_description_title);
        detailDescriptionTitle.setVisibility(View.GONE);

        addGalleryButton = (AppCompatButton) findViewById(R.id.add_gallery_image);
        addGalleryButton.setVisibility(View.GONE);

        detailDescription = (EditText) findViewById(R.id.detail_description);
        detailDescription.setText(DatabaseUtil.getCurrentItem(ItemDetailActivity.this, currentID).getDetailDescription());
        detailDescription.setEnabled(false);
        detailDescription.setBackgroundColor(getResources().getColor(R.color.white));
        detailDescription.setTextColor(getResources().getColor(R.color.black));

        recyclerView = (RecyclerView) findViewById(R.id.recycle_gallery);
        gallaryAdaper = new GallaryAdaper(ItemDetailActivity.this, DatabaseUtil.queryImagesPaths(ItemDetailActivity.this, currentID), hideEditButtons, currentID);
        recyclerView.setAdapter(gallaryAdaper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ItemDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);// Optionally customize the position you want to default scroll to
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
//        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_edit:
                Intent intent = new Intent(this, ItemEditActivity.class);
                intent.putExtra(ConstantManager.CURRENT_ITEM_ID, currentID);
                startActivity(intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
