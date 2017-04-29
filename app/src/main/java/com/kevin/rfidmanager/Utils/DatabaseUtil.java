package com.kevin.rfidmanager.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.kevin.rfidmanager.Adapter.CheckoutAdaper;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.ImagesPathDao;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.ItemsDao;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.kevin.rfidmanager.database.Users;
import com.kevin.rfidmanager.database.UsersDao;

import org.greenrobot.greendao.query.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

public class DatabaseUtil {
    /**
     * add a new user
     *
     * @param activity activity of app
     * @param username new user name
     * @param password new user password
     */
    public static boolean addNewUser(Activity activity, String username, String password) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        UsersDao usersDao = daoSession.getUsersDao();

        if (usersDao.queryBuilder().where(UsersDao.Properties.UserName.eq(username))
                .build().list().size() > 0)
            return false;

        Users user = new Users(null, username, password);
        usersDao.insert(user);
        return true;
    }

    /**
     * Query the users by username
     * @param activity activity of app
     * @param username username need to query
     * @return Users
     */
    public static List<Users> queryUsers(Activity activity, String username){
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        UsersDao usersDao = daoSession.getUsersDao();
        return usersDao.queryBuilder().where(UsersDao.Properties.UserName.eq(username)).build().
                list();
    }

    /**
     * Query the items in database.
     *
     * @return Items list
     */
    public static List<Items> queryItems(Activity activity, String user) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        Query<Items> query = itemsDao.queryBuilder().where(ItemsDao.Properties.UserName.like(user)).
                build();

        return query.list();
    }

    /**
     * Query a item by ID, if not exist, return null
     *
     * @param activity
     * @param ID
     * @return
     */
    public static Items queryItemsById(Activity activity, String ID) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        List<Items> item = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.like(ID)).
                build().list();
        if (item.size() == 1)
            return item.get(0);
        else
            return null;
    }

    /**
     * Insert new item
     *
     * @param activity activity of app
     * @param id rfid
     * @param itemName item name
     */

    public static void insertNewItem(Activity activity, String id, String itemName, String user) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        Items item = new Items(null, user, id, itemName, 0, 1, null, null);
        itemsDao.insert(item);
    }

    /**
     * Get the current item which is on focus.
     *
     * @param activity activity of app
     * @param ID rfid
     * @return items sets
     */
    public static Items getCurrentItem(Activity activity, String ID) {

        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();
        List<Items> items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.
                eq(ID)).build().list();
        if (items.size() == 1) {
            return items.get(0);
        } else if (items.size() == 0) {
            return null;
        } else {
            boolean first = true;
            for (Items item :
                    items) {
                if (first) {
                    first = false;
                    continue;
                }
                itemsDao.delete(item);
            }
            return items.get(0);
        }
    }

    /**
     * Query the key description of item in database.
     *
     * @return Items list
     */
    public static List<KeyDescription> queryItemsKeyDes(Activity activity, String RFID) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();

        Query<KeyDescription> query = keyDescriptionDao.queryBuilder().
                where(KeyDescriptionDao.Properties.Rfid.eq(RFID)).build();

        return query.list();
    }

    /**
     * Query images paths
     *
     * @param activity activity of app
     * @return images path in detail page.
     */
    public static List<ImagesPath> queryImagesPaths(Activity activity, String ID) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        return daoSession.getImagesPathDao().queryBuilder().where(ImagesPathDao.Properties.Rfid.
                eq(ID)).build().list();
    }

    /**
     * Query available inventory
     *
     * @param activity activity of app
     * @return images path in detail page.
     */
    public static int queryAvailableInventory(Activity activity, String ID) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        return daoSession.getItemsDao().queryBuilder().where(ItemsDao.Properties.Rfid.
                eq(ID)).build().list().get(0).getAvaliableInventory();
    }

    /**
     * Update new detail description into database.
     *
     * @param activity activity of app
     * @param detailDes detail description
     * @param ID rfid
     */
    public static boolean updateDetailDescription(Activity activity, String detailDes, String ID) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        Items item = getCurrentItem(activity, ID);
        if (item == null)
            return false;
        item.setDetailDescription(detailDes);
        daoSession.getItemsDao().insertOrReplace(item);
        return true;
    }

    /**
     * Update new item name into database.
     *
     * @param activity activity of app
     * @param newItemName item name
     */
    public static boolean updateItemName(Activity activity, String newItemName, String ID) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        Items item = getCurrentItem(activity, ID);
        if (item == null)
            return false;
        item.setItemName(newItemName);
        daoSession.getItemsDao().insertOrReplace(item);
        return true;
    }

    /**
     * Update the item price into database
     *
     * @param activity
     * @param itemPrice
     * @param item
     * @return
     */
    public static boolean updateItemPrice(Activity activity, float itemPrice, Items item) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        if (item == null)
            return false;
        item.setPrice(itemPrice);
        daoSession.getItemsDao().insertOrReplace(item);
        return true;
    }

    /**
     * Update the item available inventory into database
     *
     * @param activity
     * @param availableInventory
     * @param item
     * @return
     */
    public static boolean updateItemAvailableInventory(Activity activity, int availableInventory, Items item) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        if (item == null)
            return false;
        item.setAvaliableInventory(availableInventory);
        daoSession.getItemsDao().insertOrReplace(item);
        return true;
    }

    /**
     * Update the multiple items
     *
     * @param activity
     * @param items
     * @return
     */
    public static boolean updateMultiItems(Activity activity, ArrayList<CheckoutAdaper.ItemWithCount> items) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        for (CheckoutAdaper.ItemWithCount item :
                items) {
            daoSession.getItemsDao().insertOrReplace(item.getItem());
        }
        return true;
    }

    public static void importDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(context.getString(R.string.database_name));
                String backupDBPath = String.format("%s.bak",
                        context.getString(R.string.database_name));
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                ((MyApplication) context.getApplicationContext()).
                        toast(context.getString(R.string.import_success));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak",
                        context.getString(R.string.database_name));
                File currentDB = context.getDatabasePath(context.getString(R.string.database_name));
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ((MyApplication) context.getApplicationContext()).
                        toast(context.getString(R.string.backup_success));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
