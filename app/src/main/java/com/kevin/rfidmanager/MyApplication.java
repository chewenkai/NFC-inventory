package com.kevin.rfidmanager;

import android.app.Application;
import android.util.Log;

import com.kevin.rfidmanager.database.DaoMaster;
import com.kevin.rfidmanager.database.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Kevin on 2017/1/25.
 */

public class MyApplication extends Application {
    public DaoSession daoSession;  // database session
    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "rfid-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

    }

    public DaoSession getDaoSession(){
        if (daoSession == null) {
            Log.e(getClass().getName(), "daoSession is null!");
            return null;
        }
        else
            return daoSession;
    }
}
