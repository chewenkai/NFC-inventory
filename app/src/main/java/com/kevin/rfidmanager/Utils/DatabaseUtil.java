package com.kevin.rfidmanager.Utils;

import android.app.Activity;

import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPath;
import com.kevin.rfidmanager.database.ImagesPathDao;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.ItemsDao;
import com.kevin.rfidmanager.database.KeyDescription;
import com.kevin.rfidmanager.database.KeyDescriptionDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by Kevin on 2017/1/29.
 */

public class DatabaseUtil {
    /**
     * Query the items in database.
     *
     * @return Items list
     */
    public static List<Items> queryItems(Activity activity) {
        // get the items DAO
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        Query<Items> query = itemsDao.queryBuilder().where(ItemsDao.Properties.Id.isNotNull()).build();
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

        Items item = new Items(null, id, itemName, null, null);
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
            return new Items(null, 0l, "None", null, null);
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
     *
     * @param activity
     * @param detailDes
     */
    public static void updateDetailDescription(Activity activity, String detailDes){
        DaoSession daoSession = ((MyApplication) activity.getApplication()).getDaoSession();
        Items item = getCurrentItem(activity);
        item.setDetailDescription(detailDes);
        daoSession.getItemsDao().insertOrReplace(item);
    }
}
