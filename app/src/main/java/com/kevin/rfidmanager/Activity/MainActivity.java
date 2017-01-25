package com.kevin.rfidmanager.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ExitApplication;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.ItemsDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);

    }

    /**
     * Query the items in database.
     * @return Items list
     */
    public List<Items> queryItems(){
        // get the items DAO
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        ItemsDao itemsDao = daoSession.getItemsDao();

        Query<Items> query = itemsDao.queryBuilder().where(ItemsDao.Properties.Id.isNotNull()).build();
        List<Items> allItems = query.list();

        return allItems;
    }
}
