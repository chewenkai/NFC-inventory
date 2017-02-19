package com.kevin.rfidmanager.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

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
import java.util.List;

/**
 * Created by Kevin on 2017/1/29.
 * Mail: chewenkaich@gmail.com
 */

public class DatabaseUtil {
    /**
     * add a new user
     *
     * @param activity
     * @param username
     * @param password
     */
    public static boolean addNewUser(Activity activity, String username, String password) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        UsersDao usersDao = daoSession.getUsersDao();

        if (usersDao.queryBuilder().where(UsersDao.Properties.UserName.eq(username)).build().list().size() > 0)
            return false;

        Users user = new Users(null, username, password);
        usersDao.insert(user);
        return true;
    }

    /**
     * Query the users by username
     * @param activity
     * @param username
     * @return
     */
    public static List<Users> queryUsers(Activity activity, String username){
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        UsersDao usersDao = daoSession.getUsersDao();
        return usersDao.queryBuilder().where(UsersDao.Properties.UserName.eq(username)).build().list();
    }

    /**
     * Query the items in database.
     *
     * @return Items list
     */
    public static List<Items> queryItems(Activity activity) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        String userName = ((MyApplication) activity.getApplication()).getUserName();
        Query<Items> query = itemsDao.queryBuilder().where(ItemsDao.Properties.UserName.eq(userName)).build();
        List<Items> allItems = query.list();

        return allItems;
    }

    /**
     * Insert new item
     *
     * @param activity
     * @param id
     * @param itemName
     */

    public static void insertNewItem(Activity activity, long id, String itemName) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        Items item = new Items(null, ((MyApplication) activity.getApplication()).getUserName(), id, itemName, null, null);
        itemsDao.insert(item);
    }

    /**
     * Get the current item which is on focus.
     *
     * @param activity
     * @return
     */
    public static Items getCurrentItem(Activity activity) {

        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();
        List<Items> items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.
                eq(((MyApplication) activity.getApplication()).getCurrentItemID())).build().list();
        if (items.size() == 1) {
            return items.get(0);
        } else if (items.size() == 0) {
            return new Items(null, ((MyApplication) activity.getApplication()).getUserName(), 0l, "None", null, null);
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
    public static List<KeyDescription> queryItemsKeyDes(Activity activity, long RFID) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        KeyDescriptionDao keyDescriptionDao = daoSession.getKeyDescriptionDao();

        Query<KeyDescription> query = keyDescriptionDao.queryBuilder().
                where(KeyDescriptionDao.Properties.Rfid.eq(RFID)).build();
        List<KeyDescription> allItems = query.list();

        return allItems;
    }

    /**
     * Query images paths
     *
     * @param activity
     * @return
     */
    public static List<ImagesPath> queryImagesPaths(Activity activity) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        return daoSession.getImagesPathDao().queryBuilder().where(ImagesPathDao.Properties.Rfid.
                eq(((MyApplication) activity.getApplication()).getCurrentItemID())).build().list();
    }

    /**
     * Update new detail description into database.
     *
     * @param activity
     * @param detailDes
     */
    public static void updateDetailDescription(Activity activity, String detailDes) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        Items item = getCurrentItem(activity);
        item.setDetailDescription(detailDes);
        daoSession.getItemsDao().insertOrReplace(item);
    }

    /**
     * Update new item name into database.
     *
     * @param activity
     * @param newItemName
     */
    public static void updateItemName(Activity activity, String newItemName) {
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        Items item = getCurrentItem(activity);
        item.setItemName(newItemName);
        daoSession.getItemsDao().insertOrReplace(item);
    }

    public static void importDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(context.getString(R.string.database_name));
                String backupDBPath = String.format("%s.bak", context.getString(R.string.database_name));
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                ((MyApplication) context.getApplicationContext()).toast(context.getString(R.string.import_success));
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak", context.getString(R.string.database_name));
                File currentDB = context.getDatabasePath(context.getString(R.string.database_name));
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ((MyApplication) context.getApplicationContext()).toast(context.getString(R.string.backup_success));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
