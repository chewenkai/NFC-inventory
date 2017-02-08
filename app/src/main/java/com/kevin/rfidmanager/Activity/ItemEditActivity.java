package com.kevin.rfidmanager.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kevin.rfidmanager.Adapter.GallaryAdaper;
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import at.markushi.ui.CircleButton;

import static com.kevin.rfidmanager.Utils.ConstantManager.PERMISSION_REQUEST_CODE;

public class ItemEditActivity extends AppCompatActivity {
    private TextView addKeyDes, detailDescriptionTitle;
    private ListView key_des_list;
    private ImageView mainImage;
    private AppCompatButton addGalleryButton;
    private EditText itemName,detailDescription;

    private RecyclerView recyclerView;
    private GallaryAdaper gallaryAdaper;
    private KeyDesListAdapter desListAdapter;

    private ImagePicker imageGalleryPicker = null;

    private boolean hideButtons = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_layout);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle(R.string.edit_page);
        initUI();
    }

    private void initUI() {

        if (((MyApplication) getApplication()).getCurrentItemID() ==
                ConstantManager.DEFAULT_RFID)
            return;

        imageGalleryPicker = new ImagePicker(this);
        imageGalleryPicker.setImagePickerCallback(new ImagePickerCallback() {
                                                      @Override
                                                      public void onImagesChosen(List<ChosenImage> images) {
                                                          // Display images
                                                          for (ChosenImage image :
                                                                  images) {
                                                              if (image.getRequestId() == ConstantManager.REQUEST_GALLERY_IMAGE_FILE) {
                                                                  ImagesPath imagePath = new ImagesPath(null,
                                                                          ((MyApplication) getApplication()).getCurrentItemID(),
                                                                          image.getOriginalPath());
                                                                  // Add file path to database
                                                                  DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                                                                  daoSession.insert(imagePath);
                                                                  gallaryAdaper.updateUI();
                                                              }
                                                              else if (image.getRequestId() == ConstantManager.REQUEST_MAIN_IMAGE_FILE){
                                                                  // Add file path to database
                                                                  DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                                                                  Items item = DatabaseUtil.getCurrentItem(ItemEditActivity.this);
                                                                  item.setMainImagePath(image.getOriginalPath());
                                                                  daoSession.insertOrReplace(item);

                                                                  if (ContextCompat.checkSelfPermission(ItemEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                                          == PackageManager.PERMISSION_GRANTED) {
                                                                      Picasso.with(ItemEditActivity.this).load(new File(item.getMainImagePath())).resize(ScreenUtil.getScreenWidth(ItemEditActivity.this)/2,0).into(mainImage);
                                                                  } else {
                                                                      Picasso.with(ItemEditActivity.this).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(ItemEditActivity.this)/2,0).into(mainImage);
                                                                  }
                                                              }
                                                          }
                                                      }

                                                      @Override
                                                      public void onError(String message) {
                                                          // Do error handling
                                                      }
                                                  }
        );
        imageGalleryPicker.shouldGenerateMetadata(false); // Default is true
        imageGalleryPicker.shouldGenerateThumbnails(false); // Default is true

        itemName = (EditText) findViewById(R.id.item_name);
        itemName.setText(DatabaseUtil.getCurrentItem(ItemEditActivity.this).getItemName());

        key_des_list = (ListView) findViewById(R.id.listview_item_key_des);
        desListAdapter = new KeyDesListAdapter(ItemEditActivity.this,
                DatabaseUtil.queryItemsKeyDes(ItemEditActivity.this,
                        ((MyApplication) getApplication()).getCurrentItemID()), hideButtons);
        key_des_list.setAdapter(desListAdapter);
        if (desListAdapter.getCount()>0)
            key_des_list.setMinimumHeight(ScreenUtil.dpToPx(ItemEditActivity.this, 50));
        desListAdapter.setCurrentActivity(ItemEditActivity.this);

        mainImage = (ImageView) findViewById(R.id.iamgeview_main_image);
        String mainImagePath = DatabaseUtil.getCurrentItem(ItemEditActivity.this).getMainImagePath();
        if (mainImagePath != null) {
            if (ContextCompat.checkSelfPermission(ItemEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(ItemEditActivity.this).load(new File(mainImagePath)).resize(ScreenUtil.getScreenWidth(ItemEditActivity.this)/2,0).into(mainImage);
            } else {
                Picasso.with(ItemEditActivity.this).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(ItemEditActivity.this)/2,0).into(mainImage);
            }
        }
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(ItemEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ItemEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    pickMainImage();
                }
            }
        });

        addKeyDes = (TextView) findViewById(R.id.button_add_item_key_des);
        addKeyDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packUpImm();
                addNewKeyDesDialog();
            }
        });

        addGalleryButton = (AppCompatButton) findViewById(R.id.add_gallery_image);
        addGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(ItemEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ItemEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    pickImage();
//                    new MaterialFilePicker()
//                            .withSupportFragment(ItemsEditFrag.this)
//                            .withRequestCode(ConstantManager.REQUEST_GALLERY_IMAGE_FILE)
//                            .withFilter(Pattern.compile(getResources().getString(R.string.image_regexp))) // Filtering files and directories by file name using regexp
//                            .withFilterDirectories(false) // Set directories filterable (false by default)
//                            .withHiddenFiles(true) // Show hidden files and folders
//                            .start();
                }

            }
        });

        detailDescriptionTitle = (TextView) findViewById(R.id.detail_description_title);

        detailDescription = (EditText) findViewById(R.id.detail_description);
        detailDescription.setText(DatabaseUtil.getCurrentItem(ItemEditActivity.this).getDetailDescription());

        recyclerView = (RecyclerView) findViewById(R.id.recycle_gallery);
        gallaryAdaper = new GallaryAdaper(ItemEditActivity.this, DatabaseUtil.queryImagesPaths(ItemEditActivity.this), hideButtons);
        recyclerView.setAdapter(gallaryAdaper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ItemEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);// Optionally customize the position you want to default scroll to
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView.setHasFixedSize(true);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_IMAGE_DEVICE &&
                resultCode == Activity.RESULT_OK) {
            imageGalleryPicker.submit(data);
        }
    }

    /*
    This is a dialog used for add new key description
     */
    public void addNewKeyDesDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemEditActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_edit_key_des, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText newKeyDes = (TextInputEditText) dialogView.findViewById(R.id.edit_key_des_text_editor);

        final Button saveButton = (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton = (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(R.string.dialog_title_add_key_des);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewItemKeyDes(newKeyDes.getText().toString());
                desListAdapter.updateKeyDescriptionList();
                b.dismiss();
                packUpImm();
                itemName.clearFocus();

            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    /**
     *  Insert the new Item Key Description into database.
     * @param newDes
     */
    public void insertNewItemKeyDes(String newDes) {
        KeyDescription keyDescription = new KeyDescription(null,
                ((MyApplication) getApplication()).getCurrentItemID(), newDes);
        // get the key description DAO
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();
        keyDescriptionDao.insert(keyDescription);
    }

    private void pickImage() {
        imageGalleryPicker.allowMultiple(); // Default is false
        imageGalleryPicker.setRequestId(ConstantManager.REQUEST_GALLERY_IMAGE_FILE);
        imageGalleryPicker.pickImage();
    }

    private void pickMainImage() {
        imageGalleryPicker.allowMultiple();
        imageGalleryPicker.setRequestId(ConstantManager.REQUEST_MAIN_IMAGE_FILE);
        imageGalleryPicker.pickImage();
    }

    /*
    Hide input method
     */
    private void packUpImm() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_save:
                packUpImm();
                DatabaseUtil.updateItemName(ItemEditActivity.this, itemName.getText().toString());
                DatabaseUtil.updateDetailDescription(ItemEditActivity.this, detailDescription.getText().toString());
                Toast.makeText(ItemEditActivity.this, R.string.saved_item, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ItemDetailActivity.class));
                finish();
                break;
            case android.R.id.home:
                startActivity(new Intent(this, ItemDetailActivity.class));
                finish();
                break;
        }
        return true;
    }
}
