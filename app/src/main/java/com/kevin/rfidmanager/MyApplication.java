package com.kevin.rfidmanager;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.kevin.rfidmanager.database.DaoMaster;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.ImagesPathDao;
import com.kevin.rfidmanager.database.ItemsDao;
import com.kevin.rfidmanager.database.KeyDescriptionDao;
import com.kevin.rfidmanager.database.UsersDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Kevin on 2017/1/25.
 * Mail: chewenkaich@gmail.com
 */

public class MyApplication extends Application {

    public DaoSession daoSession;  // database session

    @Override
    public void onCreate() {
        super.onCreate();

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this,
                getResources().getString(R.string.database_name), null);
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

    public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            MigrationHelper.migrate(db, ItemsDao.class, KeyDescriptionDao.class, ImagesPathDao.class,
                    UsersDao.class);
        }
    }

}
