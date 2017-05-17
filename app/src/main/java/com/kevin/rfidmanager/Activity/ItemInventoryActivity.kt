package com.kevin.rfidmanager.Activity

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import at.markushi.ui.CircleButton
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.fs.FileSystem
import com.github.mjdev.libaums.fs.UsbFile
import com.github.mjdev.libaums.fs.UsbFileOutputStream
import com.github.mjdev.libaums.fs.UsbFileStreamFactory
import com.github.yuweiguocn.library.greendao.MigrationHelper
import com.kevin.rfidmanager.Adapter.ItemListAdaper
import com.kevin.rfidmanager.Adapter.NewCardListAdapter
import com.kevin.rfidmanager.Adapter.StorageDevicesAdaper
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.*
import com.kevin.rfidmanager.database.*
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.ButtonEnum
import com.nightonke.boommenu.Piece.PiecePlaceEnum
import com.rfid.def.ApiErrDefinition
import kotlinx.android.synthetic.main.item_inventory_list_layout.*
import org.jetbrains.anko.onClick
import java.io.*
import java.util.*

/**
 * Main page of the app
 */
class ItemInventoryActivity : AppCompatActivity() {
    private var actionBarTitle: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var itemListAdapter: ItemListAdaper? = null
    val items: MutableList<Items> = ArrayList<Items>()
    var currentTags = ArrayList<String>()
    private var storageDevicesAdaper: StorageDevicesAdaper? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var deleteItemsButton: CircleButton? = null
    var currentUser = ConstantManager.DEFAULT_USER
    var currentID = ConstantManager.DEFAULT_RFID
    // Read RFID PART
    var newCardsIDsStringList = ArrayList<String>()
    var newCardListAdapter: NewCardListAdapter? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_inventory_list_layout)
        initActionBar()
        initUI()
        initAddItemDialog()
//        initNFC()
    }

    private fun initActionBar() {
        val mActionBar = supportActionBar!!
        mActionBar.setDisplayShowHomeEnabled(false)
        mActionBar.setDisplayShowTitleEnabled(false)
        val mInflater = LayoutInflater.from(this)

        val actionBar = mInflater.inflate(R.layout.custom_action_bar, null)
        val mTitleTextView = actionBar.findViewById(R.id.title_text) as TextView
        actionBarTitle = mTitleTextView
        mTitleTextView.setText(R.string.app_name)
        mTitleTextView.setTextColor(resources.getColor(R.color.black))
        mActionBar.customView = actionBar
        mActionBar.setDisplayShowCustomEnabled(true)
        (actionBar.parent as Toolbar).setContentInsetsAbsolute(0, 0)

        val paddingPixels = ScreenUtil.dpToPx(this, 5)
        val leftBmb = actionBar.findViewById(R.id.action_bar_left_bmb) as BoomMenuButton

        leftBmb.buttonEnum = ButtonEnum.Ham
        leftBmb.piecePlaceEnum = PiecePlaceEnum.HAM_6
        leftBmb.buttonPlaceEnum = ButtonPlaceEnum.HAM_6

        val changeAppearance = HamButton.Builder()
                .listener { changeApperanceDialog() }
                .normalImageRes(R.drawable.ic_color_lens_white_48dp)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_apperance)
                .containsSubText(false)
        leftBmb.addBuilder(changeAppearance)

        val backup = HamButton.Builder()
                .listener { backupDialog() }
                .normalImageRes(R.drawable.ic_settings_backup_restore_white_48dp)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.backup_database)
                .containsSubText(false)
        leftBmb.addBuilder(backup)

        val restore = HamButton.Builder()
                .listener { restoreDialog() }
                .normalImageRes(R.drawable.ic_restore_white_48dp)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.restore_database)
                .containsSubText(false)
        leftBmb.addBuilder(restore)

        val changePassword = HamButton.Builder()
                .listener { showPasswordChangeDialog() }
                .normalImageRes(R.drawable.key)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_password)
                .containsSubText(false)
        leftBmb.addBuilder(changePassword)

        val change_rfid_range = HamButton.Builder()
                .listener { showRFPowerChangeDialog(); }
                .normalImageRes(R.drawable.range)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_rfid_range)
        leftBmb.addBuilder(change_rfid_range)


        val log_out = HamButton.Builder()
                .listener {
                    SPUtil.getInstence(applicationContext).saveNeedPassword(true)
                    startActivity(Intent(this@ItemInventoryActivity, LoginActivity::class.java))
                    currentID = ConstantManager.DEFAULT_RFID
                    finish()
                }
                .normalImageRes(R.drawable.logout)
                .imagePadding(Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.log_out)
        leftBmb.addBuilder(log_out)

        //        rightBmb.setButtonEnum(ButtonEnum.Ham);
        //        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        //        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        //        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
        //            rightBmb.addBuilder(BuilderManager.getHamButtonBuilder());
    }

    private fun initUI() {
        currentUser = intent.getStringExtra(ConstantManager.CURRENT_USER_NAME)
        if (currentUser == null)
            finish()
        recyclerView = findViewById(R.id.recycle_item_list) as RecyclerView
        deleteItemsButton = delete_items_button
        emptyHint.setText(R.string.inventory_empty_hint)
        (recyclerView?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        recyclerView?.itemAnimator?.endAnimations()
        setRecyclerViewLayout()
        itemListAdapter = ItemListAdaper(this@ItemInventoryActivity, items, recyclerView,
                deleteItemsButton!!, emptyHint, false)
        itemListAdapter?.setHasStableIds(true)
        recyclerView!!.adapter = itemListAdapter

        deleteItemsButton!!.setOnClickListener { itemListAdapter!!.deleteSelectedItems() }

        registUSBBroadCast()
        registNewCardsBroadCast()
        registPayResultBroadCast()
        clearCartBroadCast()
    }

    private fun clearAllRadioButtonInPowerChangeDialog(rbs: ArrayList<AppCompatRadioButton>) {
        for (rb in rbs) {
            rb.isChecked = false
        }
    }

    private fun getSelectedPower(rbs: ArrayList<AppCompatRadioButton>): Byte {
        var count = 0
        for (rb in rbs) {
            count++
            if (rb.isChecked)
                return count.toByte()
        }
        return -1
    }

    private fun registUSBBroadCast() {
        val filter = IntentFilter(ConstantManager.ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, filter)
    }

    private fun registNewCardsBroadCast() {
        val filter = IntentFilter(ConstantManager.NEW_RFID_CARD_BROADCAST_ACTION)
        registerReceiver(newCardsReceiver, filter)
    }

    private fun registPayResultBroadCast() {
        val filter = IntentFilter(ConstantManager.PAY_SUCCESSFUL_BROADCAST)
        registerReceiver(payResultReceiver, filter)
    }

    private fun clearCartBroadCast() {
        val filter = IntentFilter(ConstantManager.CLEAR_CART_BROADCAST)
        registerReceiver(clearCartReceiver, filter)
    }

    private fun initNFC(): Boolean {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            //nfc not support your device.
            return false
        }
        pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        return true
    }

    public override fun onResume() {
        super.onResume()
//        itemListAdapter!!.notifyDataSetChanged()
//        if (mNfcAdapter != null)
//            mNfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
//        if (mNfcAdapter != null) {
//            mNfcAdapter!!.disableForegroundDispatch(this)
//        }
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
                Toast.makeText(this@ItemInventoryActivity,
                        R.string.one_ID_multi_items_warning, Toast.LENGTH_LONG).show()
                return
            } else if (items.size == 1) {  // Database have an item bind with this card
                if (items[0].userName == currentUser) {
                    val id = ArrayList<String>()
                    id.add(ID)
                    updateCardsList(id)
                } else {
                    return
                }
            } else {
                val id = ArrayList<String>()
                id.add(ID)
                updateCardsList(id)
            }

            //            Parcelable[] rawMessages =
            //                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            //            if (rawMessages != null) {
            //                NdefMessage[] messages = new NdefMessage[rawMessages.length];
            //                for (int i = 0; i < rawMessages.length; i++) {
            //                    messages[i] = (NdefMessage) rawMessages[i];
            //                }
            //                Toast.makeText(ItemInventoryActivity.this, messages.toString(), Toast.LENGTH_LONG).show();
            //
            //            }
        }
        super.onNewIntent(intent)
    }

    override fun onDestroy() {
        unregisterReceiver(usbReceiver)
        unregisterReceiver(newCardsReceiver)
        unregisterReceiver(payResultReceiver)
        unregisterReceiver(clearCartReceiver)
        super.onDestroy()
    }

    private fun setRecyclerViewLayout() {
        when (SPUtil.getInstence(this@ItemInventoryActivity).apperance) {
            8  // ConstantManager.LINEAR_LAYOUT
            -> {
                val gridLayoutManager = GridLayoutManager(this@ItemInventoryActivity,
                        3)
                recyclerView!!.layoutManager = gridLayoutManager// Attach the layout manager to
            }
            9  // ConstantManager.STAGGER_LAYOUT
            -> {
                // First param is number of columns and second param is orientation i.e
                // Vertical or Horizontal
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                recyclerView!!.layoutManager = staggeredGridLayoutManager
            }
            10  // ConstantManager.ONE_ROW_LAYOUT
            -> {
                val linearLayoutManager = LinearLayoutManager(
                        this@ItemInventoryActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView!!.layoutManager = linearLayoutManager
            }
            11 -> { // ConstantManager.DETAIL_LAYOUT
                val linearLayoutManager = LinearLayoutManager(
                        this@ItemInventoryActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView!!.layoutManager = linearLayoutManager
            }
        }// the recycler view
    }

    /**
    This is a dialog used for add new key item
     */
    fun initAddItemDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_layout_two_edit_text, null)
        dialogBuilder.setView(dialogView)

        val itemName = dialogView.findViewById(R.id.item_name_edit) as TextInputEditText
        val saveButton = dialogView.findViewById(R.id.dialog_change) as Button
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as Button
        val cardsList = dialogView.findViewById(R.id.new_cards_list) as ListView
        newCardListAdapter = NewCardListAdapter(this@ItemInventoryActivity, newCardsIDsStringList)
        cardsList.adapter = newCardListAdapter

        dialogBuilder.setTitle("Add new item: ")
        dialogBuilder.setMessage("Take your RFID card close to card reader, the id will appear in the list.")
        alertDialog = dialogBuilder.create()

        saveButton.setOnClickListener(View.OnClickListener {
            val new_id = newCardListAdapter?.selectedTagId ?: ""
            if (new_id == "null" || new_id.isEmpty()) {
                Toast.makeText(this@ItemInventoryActivity,
                        "Please select at least one tag ID",
                        Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            // Are there any user info?
            val daoSession = (application as MyApplication).getmDaoSession()
            val itemsDao = daoSession.itemsDao
            val items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.like(new_id)).build().list()
            if (items.size > 0) {
                Toast.makeText(this@ItemInventoryActivity,
                        "The ID card is exist, please change a ID", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            DatabaseUtil.insertNewItem(this@ItemInventoryActivity,
                    new_id, itemName.text.toString(), currentUser)

            newCardListAdapter?.selectedTagId = "" // reset selected radio

            val intent = Intent(this@ItemInventoryActivity, ItemEditActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_ITEM_ID, new_id)
            startActivity(intent)
            alertDialog!!.dismiss()

        })

        cancleButton.setOnClickListener {
            alertDialog!!.dismiss()
        }
    }

    /**
     * Add new card to card ID list, then refresh the list
     */
    fun updateCardsList(cardIDs: ArrayList<String>) {
        currentTags = cardIDs

        if (alertDialog == null)
            return
        if (newCardListAdapter == null)
            return

        // Init the arraylist of items in database
        val itemsInDatabase = ArrayList<Items>()
        // Init the arraylist of un-recorded items ID
        val unRecordedItemsIDs = ArrayList<String>()

        // Are there any user info?
        (application as MyApplication).newDatabaseSession()
        val daoSession = (application as MyApplication).getmDaoSession()
        val itemsDao = daoSession.itemsDao

        for (cardID in cardIDs) {
            val items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.eq(cardID)).build().list()
            if (items.size > 1) {
//                Toast.makeText(this@ItemInventoryActivity,
//                        R.string.one_ID_multi_items_warning, Toast.LENGTH_LONG).show()
                return
            } else if (items.size == 1) {  // Database have an item bind with this card
                if (items[0].userName.equals(currentUser)) {
                    // Add item to List
                    itemsInDatabase.add(items[0])
                } else {
//                    Toast.makeText(this@ItemInventoryActivity,
//                            R.string.another_users_card, Toast.LENGTH_LONG).show()
                    return
                }
            } else {
                unRecordedItemsIDs.add(cardID)
            }
        }
        //modify the count number
        actionBarTitle?.text = getString(R.string.item_number) + itemsInDatabase.size + "PCs"

        // Notify update the item list, show the newest cards which are read from card reader.
        itemListAdapter!!.updateUI(itemsInDatabase)

        // Update the un-recorded item list, show the newest cards which are read from card reader.
        if (!unRecordedItemsIDs.isEmpty()) {
            newCardListAdapter!!.updateList(unRecordedItemsIDs)
            if (!alertDialog!!.isShowing)
                alertDialog!!.show()
        }
    }

    private fun changeApperanceDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_change_apperance_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(R.string.select_an_appearance)
        dialogBuilder.setNegativeButton(R.string.Cancel) { dialog, which -> }
        val b = dialogBuilder.create()
        val textView = dialogView.findViewById(R.id.backup_dialog_message) as TextView
        val linear_layout = dialogView.findViewById(R.id.linear_layout) as CircleButton
        val staggered_layout = dialogView.findViewById(R.id.staggered_layout) as CircleButton
        val one_row_layout = dialogView.findViewById(R.id.one_row_layout) as CircleButton
        val detail_layout = dialogView.findViewById(R.id.detail_layout) as CircleButton

        when (SPUtil.getInstence(this@ItemInventoryActivity).apperance) {
            8  // ConstantManager.LINEAR_LAYOUT
            -> textView.setText(R.string.current_selection_line)
            9  // ConstantManager.STAGGER_LAYOUT
            -> textView.setText(R.string.current_selection_staggered)
            10  // ConstantManager.ONE_ROW_LAYOUT
            -> textView.setText(R.string.current_selection_one_row)
            11 // ConstantManager.DETAIL_LAYOUT
            -> textView.setText(R.string.current_selection_detail_layout)
        }

        linear_layout.setOnClickListener {
            SPUtil.getInstence(this@ItemInventoryActivity).apperance = ConstantManager.LINEAR_LAYOUT
            (application as MyApplication).toast(getString(R.string.apperance_updated))
            initUI()
            b.dismiss()
        }

        staggered_layout.setOnClickListener {
            SPUtil.getInstence(this@ItemInventoryActivity).apperance = ConstantManager.STAGGER_LAYOUT
            (application as MyApplication).toast(getString(R.string.apperance_updated))
            initUI()
            b.dismiss()
        }

        one_row_layout.setOnClickListener {
            SPUtil.getInstence(this@ItemInventoryActivity).apperance = ConstantManager.ONE_ROW_LAYOUT
            (application as MyApplication).toast(getString(R.string.apperance_updated))
            initUI()
            b.dismiss()
        }

        detail_layout.onClick {
            SPUtil.getInstence(this@ItemInventoryActivity).apperance = ConstantManager.DETAIL_LAYOUT
            (application as MyApplication).toast(getString(R.string.apperance_updated))
            initUI()
            b.dismiss()
        }

        b.show()
    }

    /*
    This is a dialog used for changing password.
     */
    fun showPasswordChangeDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.password_change_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val oldPasswordEdt = dialogView.findViewById(R.id.old_password_editor) as EditText
        val newPasswordEdt = dialogView.findViewById(R.id.new_password_editor) as EditText
        val confirmNewPasswordEdt = dialogView.findViewById(R.id.confirm_new_password) as EditText
        val message = dialogView.findViewById(R.id.message_text_login) as TextView
        val saveButton = dialogView.findViewById(R.id.dialog_change) as Button
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as Button

        dialogBuilder.setTitle(resources.getString(R.string.change_passwd))
        val b = dialogBuilder.create()
        b.show()

        saveButton.setOnClickListener(View.OnClickListener {
            val daoSession = (application as MyApplication).getmDaoSession()
            val usersDao = daoSession.usersDao


            val users = DatabaseUtil.queryUsers(this@ItemInventoryActivity,
                    currentUser)
            if (users.size > 1) {
                (application as MyApplication).toast(getString(R.string.illegal_user))
                usersDao.deleteInTx(users)
                return@OnClickListener
            } else {
                val user = users[0]
                // check current password
                if (user.passWord != oldPasswordEdt.text.toString()) {
                    message.setText(R.string.wrong_old_password)
                    message.setTextColor(resources.getColor(R.color.warning_color))
                    return@OnClickListener
                }

                // check the emptiness of newPasswordEdt
                val newPasswdStr = newPasswordEdt!!.text.toString()
                if (StringUtil.isEmpty(newPasswdStr)) {
                    Snackbar.make(dialogView, R.string.empty_password_warning, Snackbar.LENGTH_LONG).show()
                    return@OnClickListener
                }
                // check the emptiness of confirmNewPasswordEdt
                val confirmPasswdStr = confirmNewPasswordEdt!!.text.toString()
                if (StringUtil.isEmpty(confirmPasswdStr)) {
                    Snackbar.make(dialogView, R.string.empty_password_warning, Snackbar.LENGTH_LONG).show()
                    return@OnClickListener
                }

                // check password of two text editors
                if (newPasswordEdt.text.toString() != confirmNewPasswordEdt.text.toString()) {
                    message.setText(R.string.diff_passwd)
                    message.setTextColor(resources.getColor(R.color.warning_color))
                    return@OnClickListener
                }
                //save password with edt.getText().toString();

                user.passWord = newPasswordEdt.text.toString()
                usersDao.insertOrReplace(user)
            }

            Toast.makeText(applicationContext,
                    R.string.password_updated, Toast.LENGTH_LONG).show()
            b.dismiss()
        })

        cancleButton.setOnClickListener {
            //dismiss dialog
            b.dismiss()
        }
    }

    /*
    This is a dialog used for changing RF power.
     */
    fun showRFPowerChangeDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.rf_power_change_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        val rbs = ArrayList<AppCompatRadioButton>()
        rbs.add(dialogView.findViewById(R.id.RBDot25W) as AppCompatRadioButton)
        rbs.add(dialogView.findViewById(R.id.RBDot50W) as AppCompatRadioButton)
        rbs.add(dialogView.findViewById(R.id.RBDot75W) as AppCompatRadioButton)
        rbs.add(dialogView.findViewById(R.id.RB1W) as AppCompatRadioButton)
        rbs.add(dialogView.findViewById(R.id.RB1Dot25W) as AppCompatRadioButton)
        rbs.add(dialogView.findViewById(R.id.RB1Dot50W) as AppCompatRadioButton)
        for (rb in rbs) {
            rb.onClick {
                clearAllRadioButtonInPowerChangeDialog(rbs)
                rb.isChecked = true
            }
        }

        val saveButton = dialogView.findViewById(R.id.dialog_change) as Button
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as Button

//        clearAllRadioButtonInPowerChangeDialog(rbs)
//        for (i in 1..6){
//            val mPower = i.toByte()
//            val nret = (application as MyApplication).
//                    m_reader.RDR_GetRFPower(mPower)
//            if (nret == ApiErrDefinition.NO_ERROR) {
//                rbs.get(i-1).isChecked = true
//                break
//            }
//        }

        clearAllRadioButtonInPowerChangeDialog(rbs)
//        val mPower = 0.toByte()
//        val nret = (application as MyApplication).
//                m_reader.RDR_GetRFPower(mPower)
//        if (nret != ApiErrDefinition.NO_ERROR) {
//            toast(getString(com.example.AnReaderDemo.R.string.tx_getRFPower_fail) + nret)
//            return
//        }

//        toast("Please record this number and tell Kevin:" + (mPower - 1).toString()) //1

        if (SPUtil.getInstence(this@ItemInventoryActivity).powerValue >= 0)
            rbs.get(SPUtil.getInstence(this@ItemInventoryActivity).powerValue).isChecked = true
        dialogBuilder.setTitle(resources.getString(R.string.change_power))
        val b = dialogBuilder.create()
        b.show()

        saveButton.setOnClickListener(View.OnClickListener {
            var str = ""
            val nret_set = (application as MyApplication).
                    m_reader.RDR_SetRFPower(getSelectedPower(rbs))
            if (nret_set == ApiErrDefinition.NO_ERROR) {
                str = getString(com.example.AnReaderDemo.R.string.tx_setPower_ok)
                SPUtil.getInstence(this@ItemInventoryActivity).
                        savePowerValue(getSelectedPower(rbs) - 1)
            } else {
                str = getString(com.example.AnReaderDemo.R.string.tx_setPower_fail)
            }
            Toast.makeText(applicationContext,
                    str, Toast.LENGTH_LONG).show()
            b.dismiss()
        })

        cancleButton.setOnClickListener {
            //dismiss dialog
            b.dismiss()
        }
    }


    /*
    This is a dialog used for backup database
     */
    fun backupDialog() {

        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_backup_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(R.string.select_backup_position)
        dialogBuilder.setNegativeButton(R.string.Cancel) { dialog, which -> }
        val b: AlertDialog

        val textView = dialogView.findViewById(R.id.backup_dialog_message) as TextView
        val recyclerView = dialogView.findViewById(R.id.recycle_view_storage_devices_list) as RecyclerView
        val deviceFiles = USBUtil.getDevicePathSet(this@ItemInventoryActivity)
        if (deviceFiles == null) {
            Toast.makeText(this@ItemInventoryActivity,
                    R.string.no_usb_permission,
                    Toast.LENGTH_LONG).show()
            return
        }
        storageDevicesAdaper = StorageDevicesAdaper(this@ItemInventoryActivity, deviceFiles)
        recyclerView.adapter = storageDevicesAdaper
        val layoutManager = LinearLayoutManager(this@ItemInventoryActivity,
                LinearLayoutManager.VERTICAL, false)
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0)
        recyclerView.layoutManager = layoutManager// Attach layout manager to the RecyclerView
        recyclerView.setHasFixedSize(true)
        dialogBuilder.setPositiveButton(R.string.OK) { dialogInterface, i ->
            if (storageDevicesAdaper!!.selectedDeviceRootPath != null) {
                if (storageDevicesAdaper!!.selectedDeviceRootPath!!.type == ConstantManager.DEFAULT_FILE) {
                    if (copyDBtoStorage(storageDevicesAdaper!!.selectedDeviceRootPath!!.defaultFile)) {
                        (application as MyApplication).toast(getString(R.string.backup_successful) +
                                " " + storageDevicesAdaper!!.selectedDeviceRootPath!!.deviceName)
                    } else {
                        (application as MyApplication).toast(getString(R.string.backup_failed))
                    }
                } else {
                    if (copyDBtoStorage(storageDevicesAdaper!!.selectedDeviceRootPath!!.usbFile)) {
                        (application as MyApplication).toast(getString(R.string.backup_successful) +
                                " " + storageDevicesAdaper!!.selectedDeviceRootPath!!.deviceName)
                    } else {
                        (application as MyApplication).toast(getString(R.string.backup_failed))
                    }
                }

            } else {
                (application as MyApplication).toast(getString(R.string.select_at_least_one_item))
            }
        }
        b = dialogBuilder.create()
        b.show()

    }

    /*
    This is a dialog used for backup database
     */
    fun restoreDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemInventoryActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_backup_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(R.string.select_restore_position)
        dialogBuilder.setMessage(R.string.restore_warning)
        dialogBuilder.setNegativeButton(R.string.Cancel) { dialog, which -> }
        val b: AlertDialog
        val textView = dialogView.findViewById(R.id.backup_dialog_message) as TextView
        val recyclerView = dialogView.findViewById(R.id.recycle_view_storage_devices_list) as RecyclerView
        val deviceFiles = USBUtil.getDevicePathSet(this@ItemInventoryActivity)
        if (deviceFiles == null) {
            Toast.makeText(this@ItemInventoryActivity,
                    R.string.grant_permission_warning,
                    Toast.LENGTH_LONG).show()
            return
        }
        storageDevicesAdaper = StorageDevicesAdaper(this@ItemInventoryActivity, deviceFiles)
        recyclerView.adapter = storageDevicesAdaper
        val layoutManager = LinearLayoutManager(this@ItemInventoryActivity, LinearLayoutManager.VERTICAL, false)
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0)
        recyclerView.layoutManager = layoutManager// Attach layout manager to the RecyclerView
        recyclerView.setHasFixedSize(true)
        dialogBuilder.setPositiveButton(R.string.OK) { dialog, which ->
            if (storageDevicesAdaper!!.selectedDeviceRootPath != null) {
                if (storageDevicesAdaper!!.selectedDeviceRootPath!!.type == ConstantManager.DEFAULT_FILE) {
                    if (copyDBtoAPP(storageDevicesAdaper!!.selectedDeviceRootPath!!.defaultFile,
                            textView)) {
                        (application as MyApplication).toast(getString(R.string.restore_successful))
                        initUI()
                    } else {
                        (application as MyApplication).toast(getString(R.string.restore_failed))
                    }
                } else {
                    if (copyDBtoAPP(storageDevicesAdaper!!.selectedDeviceRootPath!!.usbFile,
                            textView,
                            storageDevicesAdaper!!.selectedDeviceRootPath!!.device)) {
                        (application as MyApplication).toast(getString(R.string.restore_successful))
                        initUI()
                    } else {
                        (application as MyApplication).toast(getString(R.string.restore_failed))
                    }
                }

            } else {
                (application as MyApplication).toast(getString(R.string.select_at_least_one_item))
            }
        }
        b = dialogBuilder.create()
        b.show()

    }

    fun copyDBtoStorage(targetRoot: UsbFile): Boolean {
        try {
            val currentDB = getDatabasePath(getString(R.string.database_name))

            val backupDBName = String.format("%s.bak", getString(R.string.database_name))
            var exist = false
            var srcFile: UsbFile? = null
            for (file in targetRoot.listFiles()) {
                if (file.name == backupDBName) {
                    srcFile = file
                    exist = true
                }
            }
            if (exist)
                srcFile!!.delete()

            // write to a file
            val os = UsbFileOutputStream(targetRoot.createFile(backupDBName))
            os.write(getByteFromFile(currentDB))
            os.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun copyDBtoAPP(srcRoot: UsbFile, textView: TextView, device: UsbMassStorageDevice): Boolean {
        try {
            val backupDBPath = String.format("%s.bak", getString(R.string.database_name))
            val backupDB = getDatabasePath(getString(R.string.database_name))
            var exist = false
            var srcFile: UsbFile? = null
            for (file in srcRoot.listFiles()) {
                if (file.name == backupDBPath) {
                    srcFile = file
                    exist = true
                }
            }

            if (!exist) {
                textView.setText(R.string.no_backup_File_TF)
                return false
            }

            //update db
            updateBackupDB()

            val param = CopyTaskParam()
            param.from = srcFile
            param.to = backupDB
            //            new CopyTask(device.getPartitions().get(0).getFileSystem()).execute(param);
            //

            val out = BufferedOutputStream(FileOutputStream(param.to!!))
            val inputStream = UsbFileStreamFactory.createBufferedInputStream(
                    param.from!!, device.partitions[0].fileSystem)
            val bytes = ByteArray(4096)
            var count: Int
            count = inputStream.read(bytes)
            while (count != -1) {
                out.write(bytes, 0, count)
                count = inputStream.read(bytes)
            }

            out.close()
            inputStream.close()


            return true
        } catch (e: Exception) {
            e.printStackTrace()
            textView.text = e.message
            return false
        }

    }

    fun copyDBtoStorage(targetpath: String): Boolean {
        try {
            val currentDB = getDatabasePath(getString(R.string.database_name))

            val backupDBPath = String.format("%s.bak", getString(R.string.database_name))
            val backupDB = File(targetpath, backupDBPath)
            if (!backupDB.exists()) {
                if (!backupDB.createNewFile())
                    return false
            }
            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun copyDBtoAPP(srcPath: String, textView: TextView): Boolean {
        try {
            val backupDBPath = String.format("%s.bak", getString(R.string.database_name))
            val backupDB = getDatabasePath(getString(R.string.database_name))
            val currentDB = File(srcPath, backupDBPath)

            if (!backupDB.exists()) {
                textView.setText(R.string.no_backup_File_TF)
                return false
            }

            //update db
            updateBackupDB()

            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun updateBackupDB() {
        val helper = MyApplication.MySQLiteOpenHelper(this,
                resources.getString(R.string.database_name), null)
        val db = helper.writableDatabase
        MigrationHelper.migrate(db, ItemsDao::class.java, KeyDescriptionDao::class.java, ImagesPathDao::class.java,
                UsersDao::class.java, SaleInfoDao::class.java)
    }

    private fun exit() {
        val builder = AlertDialog.Builder(this@ItemInventoryActivity)
        builder.setMessage(R.string.exit_warning)
        builder.setPositiveButton(R.string.OK) { dialog, which -> finish() }
        builder.setNegativeButton(R.string.Cancel) { dialog, which -> }
        builder.create().show()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.itemlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bar_show_all -> {
                val intent = Intent(this@ItemInventoryActivity,
                        ItemListActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_USER_NAME, currentUser)
                startActivity(intent)
            }
            R.id.checkout -> {
                // Goto Check out Page
                intent = Intent(this@ItemInventoryActivity, CartActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_USER_NAME, currentUser)
                intent.putStringArrayListExtra(ConstantManager.CART_ITEM_RFID, itemListAdapter?.itemsIDInCart)
                startActivity(intent)
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun getByteFromFile(file: File): ByteArray {
        //init array with file length
        val bytesArray = ByteArray(file.length().toInt())

        val fis = FileInputStream(file)
        fis.read(bytesArray) //read file into bytes[]
        fis.close()

        return bytesArray
    }

    /**
     * Class to hold the files for a copy task. Holds the source and the
     * destination file.

     * @author mjahnen
     */
    private class CopyTaskParam {
        /* package */ internal var from: UsbFile? = null
        /* package */ internal var to: File? = null
    }

    /**
     * Asynchronous task to copy a file from the mass storage device connected
     * via USB to the internal storage.

     * @author mjahnen
     */
    private inner class CopyTask(private val currentFs: FileSystem)//            dialog = new ProgressDialog(ItemInventoryActivity.this);
    //            dialog.setTitle("Copying file");
    //            dialog.setMessage("Copying a file to the internal storage, this can take some time!");
    //            dialog.setIndeterminate(false);
    //            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        : AsyncTask<CopyTaskParam, Int, Void>() {

        //        private ProgressDialog dialog;
        private var param: CopyTaskParam? = null

        override fun onPreExecute() {
            //            dialog.show();
        }

        override fun doInBackground(vararg params: CopyTaskParam): Void? {
            param = params[0]
            try {
                val out = BufferedOutputStream(FileOutputStream(param!!.to!!))
                val inputStream = UsbFileStreamFactory.createBufferedInputStream(param!!.from!!, currentFs)
                val bytes = ByteArray(4096)
                var count: Int
                count = inputStream.read(bytes)

                while (count != -1) {
                    out.write(bytes, 0, count)
                }

                out.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void) {
            //            dialog.dismiss();

        }

        protected override fun onProgressUpdate(vararg values: Int?) {
            //            dialog.setMax((int) param.from.getLength());
            //            dialog.setProgress(values[0]);
        }

    }

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
                    val permissionIntent = PendingIntent.getBroadcast(this@ItemInventoryActivity, 0, Intent(
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

    /**
     * Receive notification of scanned cards
     */
    private val newCardsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ConstantManager.NEW_RFID_CARD_BROADCAST_ACTION == action) {
                val newCards = intent.getStringArrayListExtra(ConstantManager.NEW_RFID_CARD_KEY)
                updateCardsList(newCards)
            }
        }
    }

    /**
     * Receive notification of pay result
     */
    private val payResultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ConstantManager.PAY_SUCCESSFUL_BROADCAST == action) {
                val paySuc = intent.getBooleanExtra(ConstantManager.PAY_SUCCESSFUL, false)
                if (paySuc)
                    itemListAdapter?.itemsIDInCart?.clear()
            }
        }
    }

    /**
     * Receive notification of clear cart
     */
    private val clearCartReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ConstantManager.CLEAR_CART_BROADCAST == action) {
                itemListAdapter?.itemsIDInCart?.clear()
            }
        }
    }
}
