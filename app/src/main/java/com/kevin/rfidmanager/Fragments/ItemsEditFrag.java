package com.kevin.rfidmanager.Fragments;

/**
 * Created by Kevin on 2017/1/26.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.kevin.rfidmanager.Adapter.GallaryAdaper;
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.regex.Pattern;

import static com.kevin.rfidmanager.Utils.ConstantManager.PERMISSION_REQUEST_CODE;

public class ItemsEditFrag extends android.support.v4.app.Fragment {
    private TextView itemName, addKeyDes;
    private ListView key_des_list;
    private ImageView mainImage;
    private AppCompatButton addGalleryButton;
    private EditText detailDescription;

    private RecyclerView recyclerView;
    private GallaryAdaper gallaryAdaper;
    private KeyDesListAdapter desListAdapter;

    private Button saveButton;

    private View view;

    private boolean hideButtons = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.item_add_layout, container, false);
        this.view = v;
        initUI(v);
        return v;
    }

//    @Override
//    public void onResume() {
//        initUI(view);
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        getActivity().getFragmentManager().popBackStack();
//        super.onPause();
//    }

    private void initUI(View v) {
        if (getActivity() == null)
            return;

        if (((MyApplication) getActivity().getApplication()).getCurrentItemID() ==
                ConstantManager.DEFAULT_RFID)
            return;
        itemName = (TextView) v.findViewById(R.id.item_name);
        itemName.setText(DatabaseUtil.getCurrentItem(getActivity()).getItemName());

        key_des_list = (ListView) v.findViewById(R.id.listview_item_key_des);
        desListAdapter = new KeyDesListAdapter(getActivity(),
                DatabaseUtil.queryItemsKeyDes(getActivity(),
                        ((MyApplication) getActivity().getApplication()).getCurrentItemID()), hideButtons);
        key_des_list.setAdapter(desListAdapter);
        desListAdapter.setCurrentActivity(getActivity());

        mainImage = (ImageView) v.findViewById(R.id.iamgeview_main_image);
        String mainImagePath = DatabaseUtil.getCurrentItem(getActivity()).getMainImagePath();
        if (mainImagePath != null){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(getActivity()).load(new File(mainImagePath)).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                        ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(mainImage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.image_read_fail).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                        ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(mainImage);
            }
        }
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    new MaterialFilePicker()
                            .withSupportFragment(ItemsEditFrag.this)
                            .withRequestCode(ConstantManager.REQUEST_MAIN_IMAGE_FILE)
                            .withFilter(Pattern.compile(getActivity().getResources().getString(R.string.image_regexp))) // Filtering files and directories by file name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            }
        });

        addKeyDes = (TextView) v.findViewById(R.id.button_add_item_key_des);
        addKeyDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewKeyDesDialog();
            }
        });

        addGalleryButton = (AppCompatButton) v.findViewById(R.id.add_gallery_image);
        addGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    new MaterialFilePicker()
                            .withSupportFragment(ItemsEditFrag.this)
                            .withRequestCode(ConstantManager.REQUEST_GALLERY_IMAGE_FILE)
                            .withFilter(Pattern.compile(getActivity().getResources().getString(R.string.image_regexp))) // Filtering files and directories by file name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }

            }
        });

        detailDescription = (EditText) v.findViewById(R.id.detail_description);
        detailDescription.setText(DatabaseUtil.getCurrentItem(getActivity()).getDetailDescription());

        saveButton = (Button) v.findViewById(R.id.save_des);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packUpImm();
                DatabaseUtil.updateDetailDescription(getActivity(), detailDescription.getText().toString());
                Toast.makeText(getActivity(), R.string.saved_item, Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).viewPager.setCurrentItem(ConstantManager.HOME, false);
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recycle_gallery);
        gallaryAdaper = new GallaryAdaper(getActivity(), DatabaseUtil.queryImagesPaths(getActivity()), hideButtons);
        recyclerView.setAdapter(gallaryAdaper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);// Optionally customize the position you want to default scroll to
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView.setHasFixedSize(true);

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted.
//                new MaterialFilePicker()
//                        .withActivity(getActivity())
//                        .withRequestCode(ConstantManager.REQUEST_GALLERY_IMAGE_FILE)
////                        .withFilter(Pattern.compile(".*\\.jpg$")) // Filtering files and directories by file name using regexp
//                        .withFilterDirectories(false) // Set directories filterable (false by default)
//                        .withHiddenFiles(true) // Show hidden files and folders
//                        .start();
//            }
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantManager.REQUEST_MAIN_IMAGE_FILE
                && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Add file path to database
            DaoSession daoSession = ((MyApplication) getActivity().getApplication()).getDaoSession();
            Items item = DatabaseUtil.getCurrentItem(getActivity());
            item.setMainImagePath(filePath);
            daoSession.insertOrReplace(item);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(getActivity()).load(new File(item.getMainImagePath())).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                        ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(mainImage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.image_read_fail).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                        ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(mainImage);
            }
        } else if (requestCode == ConstantManager.REQUEST_GALLERY_IMAGE_FILE &&
                resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            ImagesPath imagePath = new ImagesPath(null,
                    ((MyApplication) getActivity().getApplication()).getCurrentItemID(),
                    filePath);
            // Add file path to database
            DaoSession daoSession = ((MyApplication) getActivity().getApplication()).getDaoSession();
            daoSession.insert(imagePath);
            gallaryAdaper.updateUI();
        }
    }

    /*
    This is a dialog used for add new key description
     */
    public void addNewKeyDesDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    public void insertNewItemKeyDes(String newDes) {
        KeyDescription keyDescription = new KeyDescription(null,
                ((MyApplication) getActivity().getApplication()).getCurrentItemID(), newDes);
        // get the key description DAO
        DaoSession daoSession = ((MyApplication) getActivity().getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();
        keyDescriptionDao.insert(keyDescription);
    }

    /*
    Hide input method
     */
    private void packUpImm() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void refreshUI(){
        if (view!=null)
            initUI(view);
    }
}
