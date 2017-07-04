package com.kevin.rfidmanager

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.media.AudioManager
import android.media.SoundPool
import android.widget.Toast
import com.github.yuweiguocn.library.greendao.MigrationHelper
import com.kevin.rfidmanager.Activity.LoginActivity
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.database.*
import java.util.*

/**
 * Created by Kevin on 2017/1/25.
 * Mail: chewenkaich@gmail.com
 */

class MyApplication : Application() {

    var daoSession: DaoSession? = null  // database session
    var savedCardsNumber = 0
    var currentUser = ConstantManager.DEFAULT_USER

    private var soundPool: SoundPool? = null
    private var soundID = 0

    override fun onCreate() {
        super.onCreate()

        newDatabaseSession()
//        registScreenAction()
//        emulateRFIDReaderTagReduce()
//        emulateIncreaseTag()
//        emulateDiffTag()
    }

    override fun onTerminate() {
        unregisterReceiver(screenReceiver)
        super.onTerminate()
    }

    fun newDatabaseSession() {
        val helper = MySQLiteOpenHelper(this,
                resources.getString(R.string.database_name), null)
        val db = helper.writableDb
        if (daoSession == null)
            daoSession = DaoMaster(db).newSession()
        else {
            daoSession?.clear()
            daoSession = DaoMaster(db).newSession()
        }
    }

    fun getmDaoSession(): DaoSession {
        val helper = MySQLiteOpenHelper(this,
                resources.getString(R.string.database_name), null)
        val db = helper.writableDb
        return daoSession ?: DaoMaster(db).newSession()
    }

    fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    class MySQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?) : DaoMaster.OpenHelper(context, name, factory) {

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            MigrationHelper.migrate(db, ItemsDao::class.java, KeyDescriptionDao::class.java, ImagesPathDao::class.java,
                    UsersDao::class.java, SaleInfoDao::class.java)
        }
    }

    // 播放声音池声音
    private fun playVoice() {
        val am = this
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioCurrentVolume = am
                .getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        soundPool!!.play(soundID, // 播放的音乐Id
                audioCurrentVolume, // 左声道音量
                audioCurrentVolume, // 右声道音量
                1, // 优先级，0为最低
                0, // 循环次数，0无不循环，-1无永远循环
                1f)// 回放速度，值在0.5-2.0之间，1为正常速度
    }

    private fun registScreenAction() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == Intent.ACTION_SCREEN_OFF) {
                startActivity(Intent(this@MyApplication, LoginActivity::class.java))
            }
        }

    }

    /**
     * Update the item count number in UI
     */
    fun updateSavedCardsNumber(cardIDs: ArrayList<String>) {
        try {
// Init the arraylist of items in database
            val itemsInDatabase = ArrayList<Items>()
            // Init the arraylist of un-recorded items ID
            val unRecordedItemsIDs = ArrayList<String>()

            // Are there any user info?
            val daoSession = getmDaoSession()
            val itemsDao = daoSession.itemsDao

            for (cardID in cardIDs) {

                val items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.eq(cardID)).build().list()
                if (items.size > 1) {
                    return
                } else if (items.size == 1) {  // Database have an item bind with this card
                    if (items[0].userName.equals(currentUser)) {
                        // Add item to List
                        itemsInDatabase.add(items[0])
                    } else {
                        return
                    }
                } else {
                    unRecordedItemsIDs.add(cardID)
                }
            }
            //modify the count number
            savedCardsNumber = itemsInDatabase.size
        } catch (e: Exception) {

        }

    }
}