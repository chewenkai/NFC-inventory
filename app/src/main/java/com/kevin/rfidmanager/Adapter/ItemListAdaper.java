package com.kevin.rfidmanager.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.kevin.rfidmanager.Activity.ItemDetailActivity;
import com.kevin.rfidmanager.Activity.ItemEditActivity;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPathDao;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import at.markushi.ui.CircleButton;

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
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SwipeLayout swipeLayout = holder.swipeLayout;
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.itemView.findViewById(R.id.bottom_wrapper));

        // Get the data model based on position
        final Items item = itemes.get(position);

        // Set item views based on your views and data model
        ImageView image = holder.image;
        if (item.getMainImagePath() == null){
            Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity),0).into(image);
        }else{
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Picasso.with(activity).load(new File(item.getMainImagePath())).resize(ScreenUtil.getScreenWidth(activity),0).into(image);
            } else {
                Picasso.with(activity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(activity),0).into(image);
            }
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)activity.getApplication()).setCurrentItemID(item.getRfid());
//                ((MainActivity)activity).viewPager.setCurrentItem(ConstantManager.DETAIL, false);
//                ((MainActivity)activity).adapter.tab2.refreshUI();
                activity.startActivity(new Intent(activity, ItemDetailActivity.class));
            }
        });

        holder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)activity.getApplication()).setCurrentItemID(item.getRfid());
//                ((MainActivity)activity).viewPager.setCurrentItem(ConstantManager.EDIT, false);
//                ((MainActivity)activity).adapter.tab3.refreshUI();
                activity.startActivity(new Intent(activity, ItemEditActivity.class));
            }
        });
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemDialog(item);
            }
        });
//        image.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                deleteItemDialog(item);
//                return true;
//            }
//        });

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
        public SwipeLayout swipeLayout;
        public ImageView image;
        public TextView itemName;
        public CircleButton editItem, deleteItem;
        public View itemView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.itemView = itemView;
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
            image = (ImageView) itemView.findViewById(R.id.item_thumb);
            itemName = (TextView) itemView.findViewById(R.id.list_item_name);
            editItem = (CircleButton) itemView.findViewById(R.id.edit_item);
            deleteItem = (CircleButton) itemView.findViewById(R.id.remove_item);
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
