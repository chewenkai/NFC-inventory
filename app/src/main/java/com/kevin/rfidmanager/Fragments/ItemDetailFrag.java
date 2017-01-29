package com.kevin.rfidmanager.Fragments;

/**
 * Created by Kevin on 2017/1/26.
 */

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.rfidmanager.Adapter.KeyDesListAdapter;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class ItemDetailFrag extends android.support.v4.app.Fragment {
    private TextView itemName, addKeyDes;
    private ListView key_des_list;
    private ImageView mainImage;

    private KeyDesListAdapter desListAdapter;
    private long currentItemID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_detail_layout, container, false);
        initUI(v);
        return v;
    }

    public void setItemID(long id) {
        this.currentItemID = id;
    }

    private void initUI(View v) {
        itemName = (TextView) v.findViewById(R.id.item_name);
        key_des_list = (ListView) v.findViewById(R.id.listview_item_key_des);
        mainImage = (ImageView) v.findViewById(R.id.iamgeview_main_image);
        addKeyDes = (TextView) v.findViewById(R.id.button_add_item_key_des);

        addKeyDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewKeyDesDialog();
            }
        });

        desListAdapter = new KeyDesListAdapter(getActivity(), queryItemsKeyDes());
        key_des_list.setAdapter(desListAdapter);
    }

    /*
    This is a dialog used for add new key description
     */
    public void addNewKeyDesDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_edit_key_des, null);
        dialogBuilder.setView(dialogView);

        final EditText newKeyDes = (EditText) dialogView.findViewById(R.id.edit_key_des_text_editor);

        final TextView saveButton = (TextView) dialogView.findViewById(R.id.dialog_ok);
        final TextView cancleButton = (TextView) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(R.string.dialog_title_add_key_des);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewItemKeyDes(newKeyDes.getText().toString());
                desListAdapter.updateList();

            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit system
                b.dismiss();
            }
        });
    }

    public void insertNewItemKeyDes(String newDes) {
        KeyDescription keyDescription = new KeyDescription(null, currentItemID, newDes);
        // get the key description DAO
        DaoSession daoSession = ((MyApplication) getActivity().getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();
        keyDescriptionDao.insertOrReplace(keyDescription);
    }

    /**
     * Query the items in database.
     *
     * @return Items list
     */
    public List<KeyDescription> queryItemsKeyDes() {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) getActivity().getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();

        Query<KeyDescription> query = keyDescriptionDao.queryBuilder().where(KeyDescriptionDao.Properties.Rfid.eq(currentItemID)).build();
        List<KeyDescription> allItems = query.list();

        return allItems;
    }


}
