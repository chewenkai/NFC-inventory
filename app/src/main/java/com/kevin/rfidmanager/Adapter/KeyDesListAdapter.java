package com.kevin.rfidmanager.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.SysUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;

import java.util.List;

import at.markushi.ui.CircleButton;

/**
 * Created by Kevin on 2017/1/29.
 */

public class KeyDesListAdapter extends ArrayAdapter<KeyDescription> {
    private KeyDesListAdapter instance = this;
    private Activity currentActivity = null;
    private Boolean hideButton = false;

    // View lookup cache
    private static class ViewHolder {
        TextView keyDescription;
        CircleButton edit;
        CircleButton delete;
    }

    public KeyDesListAdapter(Context context, List<KeyDescription> item_key_des, Boolean hideButton) {
        super(context, R.layout.key_description_listview_llayout, item_key_des);
        this.hideButton = hideButton;
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final KeyDescription keyDescription = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.key_description_listview_llayout, parent, false);
            viewHolder.keyDescription = (TextView) convertView.findViewById(R.id.text_key_des);
            viewHolder.edit = (CircleButton) convertView.findViewById(R.id.layout_edit_key_des);
            viewHolder.delete = (CircleButton) convertView.findViewById(R.id.layout_remove_key_des);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.keyDescription.setText(keyDescription.getKeyDescription());
        if (hideButton){
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);
        }else {
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(keyDescription);
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItemsKeyDes(keyDescription);
                    updateKeyDescriptionList();
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }

    /*
    This is a dialog used for edit key description
     */
    public void showEditDialog(final KeyDescription keyDescription) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.currentActivity);
        LayoutInflater inflater = this.currentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_edit_key_des, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText newKeyDes = (TextInputEditText) dialogView.findViewById(R.id.edit_key_des_text_editor);
        // The original key description should be shown in the text box when editing key description.
        newKeyDes.setText(keyDescription.getKeyDescription());

        final Button saveButton = (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton = (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(currentActivity.getResources().getString(R.string.dialog_title_edit_key_des));
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItemsKeyDes(keyDescription, newKeyDes.getText().toString());
                updateKeyDescriptionList();
                b.dismiss();
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
     * Update the Key Description list
     */
    public void updateKeyDescriptionList() {
        this.clear();
        this.addAll(DatabaseUtil.queryItemsKeyDes(currentActivity,
                ((MyApplication) currentActivity.getApplication()).getCurrentItemID()));
        this.notifyDataSetChanged();
    }

    /**
     * delete the items key description in database.
     */
    public void deleteItemsKeyDes(KeyDescription keyDescription) {
        // get the key description DAO
        DaoSession daoSession = ((MyApplication) currentActivity.getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();

        keyDescriptionDao.delete(keyDescription);
    }

    /**
     * Update the key description
     * @param keyDescription
     * @param newKeyDes
     */
    public void editItemsKeyDes(KeyDescription keyDescription, String newKeyDes) {
        keyDescription.setKeyDescription(newKeyDes);
        // get the key description DAO
        DaoSession daoSession = ((MyApplication) currentActivity.getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();
        keyDescriptionDao.insertOrReplace(keyDescription);
        updateKeyDescriptionList();
    }

}