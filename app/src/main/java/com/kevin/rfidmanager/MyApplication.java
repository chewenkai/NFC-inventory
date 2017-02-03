package com.kevin.rfidmanager;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.database.DaoMaster;
import com.kevin.rfidmanager.database.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Kevin on 2017/1/25.
 */

public class MyApplication extends Application {
    public DaoSession daoSession;  // database session

    public long getCurrentItemID() {
        return currentItemID;
    }

    public void setCurrentItemID(long currentItemID) {
        this.currentItemID = currentItemID;
    }

    private long currentItemID = ConstantManager.DEFAULT_RFID;
    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                getResources().getString(R.string.database_name));
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

    public void toast( String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
