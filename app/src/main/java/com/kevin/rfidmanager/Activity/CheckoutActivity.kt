package com.kevin.rfidmanager.Activity

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.KeyEvent
import android.widget.Toast
import com.kevin.rfidmanager.Adapter.CheckoutAdaper
import com.kevin.rfidmanager.Adapter.StorageDevicesAdaper
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.HexConvertUtil
import com.kevin.rfidmanager.Utils.SPUtil
import com.kevin.rfidmanager.database.ItemsDao
import kotlinx.android.synthetic.main.item_checkout_layout.*
import org.jetbrains.anko.toast

/**
 * Main page of the app
 */
class CheckoutActivity : AppCompatActivity() {
    private var itemListAdapter: CheckoutAdaper? = null
    private var storageDevicesAdaper: StorageDevicesAdaper? = null
    private var mAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    var currentUser = ConstantManager.DEFAULT_USER!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_checkout_layout)
        initUI()
        initNFC()
    }


    private fun initUI() {
        currentUser = intent.getStringExtra(ConstantManager.CURRENT_USER_NAME)
        itemListAdapter = CheckoutAdaper(this@CheckoutActivity, recycle_item_list, pay_button!!, tv_checkout_result)
        recycle_item_list.adapter = itemListAdapter
        setRecyclerViewLayout()
        recycle_item_list.setHasFixedSize(true)
        registUSBBroadCast()
        pay_button!!.setOnClickListener { itemListAdapter!!.deleteSelectedItems() }
    }

    private fun registUSBBroadCast() {
        val filter = IntentFilter(ConstantManager.ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, filter)
    }

    private fun initNFC(): Boolean {
        mAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mAdapter == null) {
            //nfc not support your device.
            return false
        }
        pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        return true
    }

    public override fun onResume() {
        super.onResume()
        if (mAdapter != null)
            mAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        if (mAdapter != null) {
            mAdapter!!.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ID = HexConvertUtil.bytesToHexString(tag.id)

            // Are there any user info?
            val daoSession = (application as MyApplication).getmDaoSession()
            val itemsDao = daoSession.itemsDao
            val items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.like(ID)).build().list()
            if (items.size > 1) {
                Toast.makeText(this@CheckoutActivity,
                        R.string.one_ID_multi_items_warning, Toast.LENGTH_LONG).show()
                return
            } else if (items.size == 1) {  // Database have an item bind with this card
                if (items[0].userName == currentUser) {
                    val item = CheckoutAdaper.ItemWithCount()
                    item.item = items[0]
                    itemListAdapter!!.addItem(item)

                } else {
                    Toast.makeText(this@CheckoutActivity,
                            R.string.another_users_card, Toast.LENGTH_LONG).show()
                    return
                }
            } else
                toast(R.string.item_have_not_added)

        }
        super.onNewIntent(intent)
    }

    override fun onDestroy() {
        unregisterReceiver(usbReceiver)
        super.onDestroy()
    }

    private fun setRecyclerViewLayout() {
        when (SPUtil.getInstence(this@CheckoutActivity).apperance) {
            8  // ConstantManager.LINEAR_LAYOUT
            -> {
                val gridLayoutManager = GridLayoutManager(this@CheckoutActivity,
                        3, GridLayoutManager.VERTICAL, false)
                recycle_item_list!!.layoutManager = gridLayoutManager// Attach the layout manager to
            }
            9  // ConstantManager.STAGGER_LAYOUT
            -> {
                // First param is number of columns and second param is orientation i.e
                // Vertical or Horizontal
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                recycle_item_list!!.layoutManager = staggeredGridLayoutManager
            }
            10  // ConstantManager.ONE_ROW_LAYOUT
            -> {
                val linearLayoutManager = LinearLayoutManager(
                        this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
                recycle_item_list!!.layoutManager = linearLayoutManager
            }
            11 -> {
                val linearLayoutManager = LinearLayoutManager(
                        this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
                recycle_item_list!!.layoutManager = linearLayoutManager
            }
        }// the recycler view
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.itemlist_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_bar_add -> {
//
//            }
//            R.id.checkout -> {
//
//            }
//        }
//        return true
//    }


    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action
            if (ConstantManager.ACTION_USB_PERMISSION == action) {

                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null) {
                        discoverDevice()
                    }
                } else {
                    val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
                    val permissionIntent = PendingIntent.getBroadcast(this@CheckoutActivity, 0, Intent(
                            ConstantManager.ACTION_USB_PERMISSION), 0)
                    usbManager.requestPermission(device, permissionIntent)
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                // determine if connected device is a mass storage devuce
                if (device != null) {
                    discoverDevice()
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)


                // determine if connected device is a mass storage devuce
                if (device != null) {
                    // check if there are other devices or set action bar title
                    // to no device if not
                    discoverDevice()
                }
            }

        }
    }

    /**
     * Refresh the list.
     */
    private fun discoverDevice() {
        if (storageDevicesAdaper != null) {
            storageDevicesAdaper!!.updateDataSet()
        }
    }

}
