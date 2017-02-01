package com.kevin.rfidmanager.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.rfidmanager.Activity.MainActivity;
import com.kevin.rfidmanager.Fragments.ItemDetailFrag;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.BitMapUtil;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ExitApplication;
import com.kevin.rfidmanager.Utils.IntentUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.ImagesPathDao;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Kevin on 2017/1/29.
 */

public class ItemListAdaper extends RecyclerView.Adapter<ItemListAdaper.ViewHolder> {
    final public Activity activity;
    List<Items> itemes;

    public ItemListAdaper(Activity activity, List<Items> itemes) {
        this.activity = activity;
        this.itemes = itemes;
    }

    public Context getContext() {
        return activity.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_adapter_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        final Items item = itemes.get(position);

        // Set item views based on your views and data model
        ImageView image = holder.image;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Picasso.with(activity).load(new File(item.getMainImagePath())).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                    ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(image);
        } else {
            Picasso.with(activity).load(R.drawable.image_read_fail).resize(ConstantManager.DEFAULT_IMAGE_WIDTH,
                    ConstantManager.DEFAULT_IMAGE_HEIGHT).centerCrop().into(image);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)activity.getApplication()).setCurrentItemID(item.getRfid());
                ((MainActivity)activity).adapter.tab2.refreshUI();
                ((MainActivity)activity).viewPager.setCurrentItem(ConstantManager.DETAIL, false);
            }
        });

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                deleteItemDialog(item);
                return true;
            }
        });

        TextView itemName = holder.itemName;
        itemName.setText(item.getItemName());
    }

    @Override
    public int getItemCount() {
        return itemes.size();
    }

    public void updateUI() {
        this.itemes.clear();
        this.itemes.addAll(DatabaseUtil.queryItems(activity));
        this.notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView image;
        public TextView itemName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.item_thumb);
            itemName = (TextView) itemView.findViewById(R.id.list_item_name);
        }
    }

    /*
    This is a dialog used for delete item
     */
    public void deleteItemDialog(final Items item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.delete_confirm_title);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
                daoSession.getItemsDao().delete(item);
                // delete image path
                daoSession.getImagesPathDao().deleteInTx(daoSession.getImagesPathDao().queryBuilder().
                where(ImagesPathDao.Properties.Rfid.eq(item.getRfid())).build().list());
                // delete key description
                daoSession.getKeyDescriptionDao().deleteInTx(daoSession.getKeyDescriptionDao().queryBuilder().
                where(KeyDescriptionDao.Properties.Rfid.eq(item.getRfid())).build().list());
                Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show();
                updateUI();
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
}
