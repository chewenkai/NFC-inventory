package com.kevin.rfidmanager

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.github.yuweiguocn.library.greendao.MigrationHelper
import com.kevin.rfidmanager.Activity.LoginActivity
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.ConstantManager.NEW_RFID_CARD_BROADCAST_ACTION
import com.kevin.rfidmanager.Utils.ConstantManager.NEW_RFID_CARD_KEY
import com.kevin.rfidmanager.database.*
import com.rfid.api.ADReaderInterface
import com.rfid.api.GFunction
import com.rfid.api.ISO15693Interface
import com.rfid.api.ISO15693Tag
import com.rfid.def.ApiErrDefinition
import com.rfid.def.RfidDef
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Kevin on 2017/1/25.
 * Mail: chewenkaich@gmail.com
 */

class MyApplication : Application() {

    var daoSession: DaoSession? = null  // database session
    var cardIDs = ArrayList<String>()
    var savedCardsNumber = 0
    var currentUser = ConstantManager.DEFAULT_USER

    // Read RFID PART
    var isReaderConnected = false
    var m_reader: ADReaderInterface = ADReaderInterface()
    private var m_inventoryThrd: Thread? = null// The thread of inventory
    private var m_getScanRecordThrd: Thread? = null// The thead of scanf the record.

    val INVENTORY_MSG = 1
    val GETSCANRECORD = 2
    val INVENTORY_FAIL_MSG = 4
    val THREAD_END = 3
    private var soundPool: SoundPool? = null
    private var soundID = 0

    // Inventory parameter
    private var bUseDefaultPara = true
    private var bOnlyReadNew = false
    private var bMathAFI = false
    private var mAFIVal: Byte = 0x00
    private var bBuzzer = true

    override fun onCreate() {
        super.onCreate()

        newDatabaseSession()
        startScan()
        initSound()
        registUSBBroadCast()
        registScreenAction()
        detectConnection()
//        emulateRFIDReaderTagReduce()
//        emulateIncreaseTag()
    }

    override fun onTerminate() {
//        unregisterReceiver(usbReceiver)
        unregisterReceiver(screenReceiver)
        stopScan()
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
        return daoSession!!
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

    // Blow is the RFID read part

    private fun detectConnection() {
        doAsync {
            while (true) {
                try {
                    Thread.sleep(5000)
                    if (!m_reader.isReaderOpen) {
                        uiThread {
                            startScan()
                        }
                    }
                } catch (e: Throwable ) {
                    uiThread {
                        toast(e.toString())
                    }
                }
            }
        }
    }
    /**
     * start scan RFID cards and show at the dialog
     */
    private fun startScan() {
        ADReaderInterface.EnumerateUsb(this)
        if (!ADReaderInterface.HasUsbPermission("")) {
            ADReaderInterface.RequestUsbPermission("")
            return
        }
        OpenDev()
        openRF()
    }

    /**
     * stop scan RFID cards and stop at the dialog
     */
    private fun stopScan() {
        // 如果盘点标签线程正在运行，则关闭该线程
        // If thread of inventory is running,stop the thread before exit the
        // application.
        if (m_inventoryThrd != null && m_inventoryThrd!!.isAlive()) {
            b_inventoryThreadRun = false
            m_reader.RDR_SetCommuImmeTimeout()
            try {
                m_inventoryThrd!!.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
        closeRF()
        CloseDev()
        b_inventoryThreadRun = false
    }

    /**
     * Open the device
     */
    private fun OpenDev() {
        var conStr = ""
        val commTypeStr: String
        var devName = ""
        devName = "TPAD"
        commTypeStr = "USB"

        if (commTypeStr == "USB") {
            // 注意：使用USB方式时，必须先要枚举所有USB设备
            // Note: Before using USB, you must enumerate all USB devices first.
            val usbCnt = ADReaderInterface.EnumerateUsb(this)
            if (usbCnt <= 0) {
                Toast.makeText(this, getString(R.string.tx_msg_noUsb),
                        Toast.LENGTH_SHORT).show()
                return
            }

            if (!ADReaderInterface.HasUsbPermission("")) {
                Toast.makeText(this,
                        getString(R.string.tx_msg_noUsbPermission),
                        Toast.LENGTH_SHORT).show()
                ADReaderInterface.RequestUsbPermission("")
                return
            }

            conStr = String.format("RDType=%s;CommType=USB;Description=",
                    devName)
        }

        if (m_reader.RDR_Open(conStr) == ApiErrDefinition.NO_ERROR) {
            // ///////////////////////只有RPAN设备支持扫描模式/////////////////////////////

            Toast.makeText(this, getString(R.string.tx_msg_openDev_ok),
                    Toast.LENGTH_SHORT).show()

//            m_getScanRecordThrd = Thread(GetScanRecordThrd())
//            m_getScanRecordThrd!!.start()
            m_inventoryThrd = Thread(InventoryThrd())
            m_inventoryThrd!!.start()

        } else {
//            Toast.makeText(this, getString(R.string.tx_msg_openDev_fail),
//                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * close the device
     */
    private fun CloseDev() {
        if (m_inventoryThrd != null && m_inventoryThrd!!.isAlive()) {
            toast(getString(R.string.tx_msg_stopInventory_tip))
            return
        }
        m_reader.RDR_Close()
    }

    /**
     * open the RF
     */
    fun openRF() {
        if (m_reader.RDR_OpenRFTransmitter() == ApiErrDefinition.NO_ERROR) {
//            str = getString(R.string.tx_openRF_ok)
//            toast(str)
        } else {
//            str = getString(R.string.tx_openRF_fail)
        }
    }

    /**
     * close the RF
     */
    fun closeRF() {
        var str = ""
        if (m_reader.RDR_CloseRFTransmitter() == ApiErrDefinition.NO_ERROR) {
            str = getString(R.string.tx_CloseRF_ok)// "关闭射频成功！";
        } else {
            str = getString(R.string.tx_CloseRF_fail)// "关闭射频失败";
        }
        toast(str)
    }

    private var bGetScanRecordFlg = false

    private inner class GetScanRecordThrd : Runnable {
        override fun run() {
            var nret = 0
            bGetScanRecordFlg = true
            var gFlg: Byte = 0x00// 初次采集数据或者上一次采集数据失败时，标志位为0x00
            var dnhReport: Any? = null

            // 清空缓冲区记录
            nret = m_reader.RPAN_ClearScanRecord()
            if (nret != ApiErrDefinition.NO_ERROR) {
                bGetScanRecordFlg = false
                mHandler.sendEmptyMessage(THREAD_END)// 盘点结束
                return
            }

            while (bGetScanRecordFlg) {
                if (mHandler.hasMessages(GETSCANRECORD)) {
                    continue
                }
                nret = m_reader.RPAN_GetRecord(gFlg)
                if (nret != ApiErrDefinition.NO_ERROR) {
                    gFlg = 0x00
                    continue
                }
                gFlg = 0x01// 数据获取成功，将标志位设置为0x01
                dnhReport = m_reader
                        .RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST)
                val dataList = Vector<String>()
                while (dnhReport != null) {
                    val byData = m_reader.RPAN_ParseRecord(dnhReport)
                    val strData = GFunction.encodeHexStr(byData)
                    dataList.add(strData)
                    dnhReport = m_reader
                            .RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT)
                }
                if (!dataList.isEmpty()) {
                    val msg = mHandler.obtainMessage()
                    msg.what = GETSCANRECORD
                    msg.obj = dataList
                    mHandler.sendMessage(msg)
                }
            }
            bGetScanRecordFlg = false
            mHandler.sendEmptyMessage(THREAD_END)// 结束
        }
    }

    /**
     * Scan the Cards at another thread.
     */
    private var b_inventoryThreadRun = false

    private inner class InventoryThrd : Runnable {
        override fun run() {
            var failedCnt = 0// 操作失败次数
            var hInvenParamSpecList: Any? = null
            var newAI = RfidDef.AI_TYPE_NEW
            if (bOnlyReadNew) {
                newAI = RfidDef.AI_TYPE_CONTINUE
            }
            if (!bUseDefaultPara) {
                hInvenParamSpecList = ADReaderInterface
                        .RDR_CreateInvenParamSpecList()
                ISO15693Interface.ISO15693_CreateInvenParam(
                        hInvenParamSpecList, 0.toByte(), bMathAFI, mAFIVal,
                        0.toByte())
            }
            b_inventoryThreadRun = true
            while (b_inventoryThreadRun) {
                if (mHandler.hasMessages(INVENTORY_MSG)) {
                    continue
                }
                var iret = m_reader.RDR_TagInventory(newAI, null, 0,
                        hInvenParamSpecList)
                if (iret == ApiErrDefinition.NO_ERROR || iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                    val tagList = Vector<ISO15693Tag>()
                    newAI = RfidDef.AI_TYPE_NEW
                    if (bOnlyReadNew || iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                        newAI = RfidDef.AI_TYPE_CONTINUE
                    }

                    var tagReport: Any? = m_reader
                            .RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST)
                    while (tagReport != null) {
                        val tagData = ISO15693Tag()
                        iret = ISO15693Interface.ISO15693_ParseTagDataReport(
                                tagReport, tagData)
                        if (iret == ApiErrDefinition.NO_ERROR) {
                            tagList.add(tagData)
                        }
                        tagReport = m_reader
                                .RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT)
                    }
                    if (!tagList.isEmpty()) {
                        if (bBuzzer) {
                            playVoice()
                        }
                        val msg = mHandler.obtainMessage()
                        msg.what = INVENTORY_MSG
                        msg.obj = tagList
                        msg.arg1 = failedCnt
                        mHandler.sendMessage(msg)
                    } else {
                        val msg = mHandler.obtainMessage()
                        msg.what = INVENTORY_MSG
                        msg.obj = tagList
                        msg.arg1 = failedCnt
                        mHandler.sendMessage(msg)
                    }
                } else {
//                    toast("tell kevin:" + iret.toString())
                    newAI = RfidDef.AI_TYPE_NEW
                    if (b_inventoryThreadRun) {
                        failedCnt++
                    }
                    val msg = mHandler.obtainMessage()
                    msg.what = INVENTORY_FAIL_MSG
                    msg.arg1 = failedCnt
                    mHandler.sendMessage(msg)

                    // reboot the rfid reader
                    m_reader.RDR_CloseRFTransmitter()
                    m_reader.RDR_Close()
                    m_reader.RDR_Open(String.format("RDType=%s;CommType=USB;Description=", "TPAD"))
                    m_reader.RDR_OpenRFTransmitter()
                    try {
                        Thread.sleep(2000)  // delay two seconds
                    } catch (e: InterruptedException) {
                    }

                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }

            }
            b_inventoryThreadRun = false
            m_reader.RDR_ResetCommuImmeTimeout()
            mHandler.sendEmptyMessage(THREAD_END)// 盘点结束
        }
    }

    private val mHandler = MyHandler(this)

    private class MyHandler(application: MyApplication) : Handler() {
        private val thisApplicaiton: WeakReference<MyApplication>

        init {
            thisApplicaiton = WeakReference(application)
        }

        override fun handleMessage(msg: Message) {
            val myApplication = thisApplicaiton.get() ?: return
            when (msg.what) {
                myApplication.INVENTORY_MSG -> { // 盘点到标签
                    val tagList = msg.obj as Vector<ISO15693Tag>
                    val cardIDs = ArrayList<String>()
                    for (tagData in tagList) {
                        cardIDs.add(GFunction.encodeHexStr(tagData.uid))
                    }
                    myApplication.cardIDs = cardIDs
                    myApplication.updateSavedCardsNumber(cardIDs)
                    val intent = Intent().setAction(NEW_RFID_CARD_BROADCAST_ACTION)
                    intent.putStringArrayListExtra(NEW_RFID_CARD_KEY, cardIDs)
                    myApplication.sendBroadcast(intent)
                }

                myApplication.INVENTORY_FAIL_MSG -> Toast.makeText(myApplication,
                        "read failed", Toast.LENGTH_LONG).show()

                myApplication.THREAD_END -> null // 线程结束
                else -> {
                }
            }
        }
    }

    fun emulateIncreaseTag() {
        val cardIDs = ArrayList<String>()
        var count = 1

        doAsync {
            while (true) {

                try {
                    Thread.sleep(500)
                    uiThread {
                        while (count < 10) {
                            cardIDs.add(count.toString())
                            count++
                        }
                        this@MyApplication.cardIDs = cardIDs
                        updateSavedCardsNumber(cardIDs)
                        val intent = Intent().setAction(NEW_RFID_CARD_BROADCAST_ACTION)
                        intent.putStringArrayListExtra(NEW_RFID_CARD_KEY, cardIDs)
                        sendBroadcast(intent)
                    }
                } catch (e: InterruptedException) {
                }
            }
            // Long background task
        }
    }

    /**
     * id deduct from 10 to 1
     */
    fun emulateRFIDReaderTagReduce() {
        val cardIDs = ArrayList<String>()
        var count = 1
        while (count < 10) {
            cardIDs.add(count.toString())
            count++
        }
        doAsync {
            while (true) {

                try {
                    Thread.sleep(5000)
                    uiThread {
                        if (cardIDs.size > 0) {
                            cardIDs.removeAt(cardIDs.size - 1)
                        }

                        this@MyApplication.cardIDs = cardIDs
                        updateSavedCardsNumber(cardIDs)
                        val intent = Intent().setAction(NEW_RFID_CARD_BROADCAST_ACTION)
                        intent.putStringArrayListExtra(NEW_RFID_CARD_KEY, cardIDs)
                        sendBroadcast(intent)
                    }
                } catch (e: InterruptedException) {
                }
            }
            // Long background task
        }
    }

    private fun initSound() {
        // 初始化声音
        // Initialize the sound
        soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 5)
        soundID = soundPool!!.load(this, R.raw.msg, 1)
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
    private fun registUSBBroadCast() {
        val filter = IntentFilter(ConstantManager.ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, filter)
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action
            if (ConstantManager.ACTION_USB_PERMISSION == action) {

                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null) {
                        startScan()
                    }
                } else {
                    val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
                    val permissionIntent = PendingIntent.getBroadcast(this@MyApplication, 0, Intent(
                            ConstantManager.ACTION_USB_PERMISSION), 0)
                    usbManager.requestPermission(device, permissionIntent)
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                // determine if connected device is a mass storage devuce
                if (device != null) {
                    startScan()
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)


                // determine if connected device is a mass storage devuce
                if (device != null) {
                    // check if there are other devices or set action bar title
                    // to no device if not
                    startScan()
                }
            }

        }
    }

    /**
     * Update the item count number in UI
     */
    fun updateSavedCardsNumber(cardIDs: ArrayList<String>) {
        // Init the arraylist of items in database
        val itemsInDatabase = ArrayList<Items>()
        // Init the arraylist of un-recorded items ID
        val unRecordedItemsIDs = ArrayList<String>()

        // Are there any user info?
        val daoSession = getmDaoSession()
        val itemsDao = daoSession.itemsDao

        for (cardID in cardIDs) {
            val items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.like(cardID)).build().list()
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
    }
}