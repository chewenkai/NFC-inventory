package com.kevin.rfidmanager;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.database.DaoMaster;
import com.kevin.rfidmanager.database.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Kevin on 2017/1/25.
 * Mail: chewenkaich@gmail.com
 */

public class MyApplication extends Application {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public DaoSession daoSession;  // database session

    public String getCurrentItemID() {
        return currentItemID;
    }

    public void setCurrentItemID(String currentItemID) {
        this.currentItemID = currentItemID;
    }

    private String currentItemID = ConstantManager.DEFAULT_RFID;

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
