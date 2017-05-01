package com.kevin.rfidmanager.Activity

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.kbeanie.multipicker.api.ImagePicker
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback
import com.kbeanie.multipicker.api.entity.ChosenImage
import com.kevin.rfidmanager.Adapter.GallaryAdaper
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.ConstantManager.PERMISSION_REQUEST_CODE
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.kevin.rfidmanager.database.ImagesPath
import com.kevin.rfidmanager.database.Items
import com.kevin.rfidmanager.database.KeyDescription
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_add_layout.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor
import java.io.File

class ItemEditActivity : AppCompatActivity() {
    private var item: Items? = null
    private var addKeyDes: TextView? = null
    private var detailDescriptionTitle: TextView? = null
    private var key_des_list: ListView? = null
    private var mainImage: ImageView? = null
    private var addGalleryButton: AppCompatButton? = null
    private var itemName: EditText? = null
    private var detailDescription: EditText? = null

    private var recyclerView: RecyclerView? = null
    private var gallaryAdaper: GallaryAdaper? = null
    private var desListAdapter: KeyDesListAdapter? = null

    private var imageGalleryPicker: ImagePicker? = null

    var currentID = ConstantManager.DEFAULT_RFID

    private val hideButtons = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_add_layout)
        val actionBar = supportActionBar!!
        actionBar.setTitle(R.string.edit_page)
        initUI()
        registNewCardsBroadCast()
    }

    override fun onDestroy() {
        unregisterReceiver(newCardsReceiver)
        super.onDestroy()
    }

    private fun initUI() {
        // IMPORTANT: Below Line Must At The First Line Of The Method!
        currentID = intent.getStringExtra(ConstantManager.CURRENT_ITEM_ID)
        if (currentID == ConstantManager.DEFAULT_RFID)
            return

        // Get and check the current item
        item = DatabaseUtil.getCurrentItem(this@ItemEditActivity, currentID)
        if (item == null) {
            Toast.makeText(this@ItemEditActivity, R.string.item_not_exist, Toast.LENGTH_LONG).show()
            return
        }

        imageGalleryPicker = ImagePicker(this)
        imageGalleryPicker!!.setImagePickerCallback(object : ImagePickerCallback {
            override fun onImagesChosen(images: List<ChosenImage>?) {
                if (images == null)
                    return
                // Display images
                for (image in images) {
                    if (image.requestId == ConstantManager.REQUEST_GALLERY_IMAGE_FILE) {
                        val imagePath = ImagesPath(null, currentID,
                                image.originalPath)
                        // Add file path to database
                        val daoSession = (application as MyApplication).getmDaoSession()
                        daoSession.imagesPathDao.insert(imagePath)
                        gallaryAdaper!!.updateUI()
                    } else if (image.requestId == ConstantManager.REQUEST_MAIN_IMAGE_FILE) {
                        // Add file path to database
                        val daoSession = (application as MyApplication).getmDaoSession()
                        val item = DatabaseUtil.getCurrentItem(this@ItemEditActivity, currentID)
                        if (item == null) {
                            Toast.makeText(this@ItemEditActivity, R.string.item_not_exist, Toast.LENGTH_LONG).show()
                            return
                        }
                        item.mainImagePath = image.originalPath
                        daoSession.itemsDao.insertOrReplace(item)

                        if (ContextCompat.checkSelfPermission(this@ItemEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Picasso.with(this@ItemEditActivity).load(File(item.mainImagePath)).resize(ScreenUtil.getScreenWidth(this@ItemEditActivity) / 2, 0).into(mainImage)
                        } else {
                            Picasso.with(this@ItemEditActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemEditActivity) / 2, 0).into(mainImage)
                        }
                    }
                }
            }

            override fun onError(message: String) {
                // Do error handling
            }
        }
        )

        et_available_inventory.enabled = true
        et_available_inventory.textColor = resources.getColor(R.color.black)
        et_available_inventory.setText(item!!.avaliableInventory.toString())

        et_price.enabled = true
        et_price.textColor = resources.getColor(R.color.black)
        et_price.setText(item!!.price.toInt().toString())

        imageGalleryPicker!!.shouldGenerateMetadata(false) // Default is true
        imageGalleryPicker!!.shouldGenerateThumbnails(false) // Default is true

        itemName = findViewById(R.id.item_name) as EditText
        itemName!!.setText(item!!.itemName)

        key_des_list = findViewById(R.id.listview_item_key_des) as ListView
        desListAdapter = KeyDesListAdapter(this@ItemEditActivity,
                DatabaseUtil.queryItemsKeyDes(this@ItemEditActivity, currentID), hideButtons,
                currentID)
        key_des_list!!.adapter = desListAdapter
        if (desListAdapter!!.count > 0)
            key_des_list!!.minimumHeight = ScreenUtil.dpToPx(this@ItemEditActivity, 50)
        desListAdapter!!.setCurrentActivity(this@ItemEditActivity)

        mainImage = findViewById(R.id.iamgeview_main_image) as ImageView

        val mainImagePath = item!!.mainImagePath
        if (mainImagePath != null) {
            if (ContextCompat.checkSelfPermission(this@ItemEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // check if the file exist
                if (File(mainImagePath).exists()) {
                    Picasso.with(this@ItemEditActivity).load(File(mainImagePath)).resize(ScreenUtil.getScreenWidth(this@ItemEditActivity) / 2, 0).into(mainImage)
                } else {
                    Picasso.with(this@ItemEditActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemEditActivity) / 2, 0).into(mainImage)
                }
            } else {
                Picasso.with(this@ItemEditActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemEditActivity) / 2, 0).into(mainImage)
            }
        }
        mainImage!!.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this@ItemEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ItemEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                pickMainImage()
            }
        }

        addKeyDes = findViewById(R.id.button_add_item_key_des) as TextView
        addKeyDes!!.setOnClickListener {
            packUpImm()
            addNewKeyDesDialog()
        }

        addGalleryButton = findViewById(R.id.add_gallery_image) as AppCompatButton
        addGalleryButton!!.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this@ItemEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ItemEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                pickImage()
                //                    new MaterialFilePicker()
                //                            .withSupportFragment(ItemsEditFrag.this)
                //                            .withRequestCode(ConstantManager.REQUEST_GALLERY_IMAGE_FILE)
                //                            .withFilter(Pattern.compile(getResources().getString(R.string.image_regexp))) // Filtering files and directories by file name using regexp
                //                            .withFilterDirectories(false) // Set directories filterable (false by default)
                //                            .withHiddenFiles(true) // Show hidden files and folders
                //                            .start();
            }
        }

        detailDescriptionTitle = findViewById(R.id.detail_description_title) as TextView

        detailDescription = findViewById(R.id.detail_description) as EditText
        detailDescription!!.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        detailDescription!!.setText(item!!.detailDescription)

        recyclerView = findViewById(R.id.recycle_gallery) as RecyclerView
        gallaryAdaper = GallaryAdaper(this@ItemEditActivity, DatabaseUtil.queryImagesPaths(this@ItemEditActivity, currentID), hideButtons, currentID)
        recyclerView!!.adapter = gallaryAdaper
        val layoutManager = LinearLayoutManager(this@ItemEditActivity, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.scrollToPosition(0)// Optionally customize the position you want to default scroll to
        recyclerView!!.layoutManager = layoutManager// Attach layout manager to the RecyclerView
        val gridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)// First param is number of columns and second param is orientation i.e Vertical or Horizontal
        recyclerView!!.layoutManager = gridLayoutManager// Attach the layout manager to the recycler view
        recyclerView!!.setHasFixedSize(true)


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == Activity.RESULT_OK) {
            if (data != null)
                imageGalleryPicker!!.submit(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*
    This is a dialog used for add new key description
     */
    fun addNewKeyDesDialog() {
        val dialogBuilder = AlertDialog.Builder(this@ItemEditActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_layout_edit_key_des, null)
        dialogBuilder.setView(dialogView)

        val newKeyDes = dialogView.findViewById(R.id.edit_key_des_text_editor) as TextInputEditText

        val saveButton = dialogView.findViewById(R.id.dialog_change) as Button
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as Button

        dialogBuilder.setTitle(R.string.dialog_title_add_key_des)
        val b = dialogBuilder.create()
        b.show()

        saveButton.setOnClickListener {
            insertNewItemKeyDes(newKeyDes.text.toString())
            desListAdapter!!.updateKeyDescriptionList()
            b.dismiss()
            packUpImm()
            itemName!!.clearFocus()
        }

        cancleButton.setOnClickListener { b.dismiss() }
    }

    /**
     * Insert the new Item Key Description into database.
     * @param newDes new description
     */
    fun insertNewItemKeyDes(newDes: String) {
        val keyDescription = KeyDescription(null, currentID, newDes)
        // get the key description DAO
        val daoSession = (application as MyApplication).getmDaoSession()
        val keyDescriptionDao = daoSession.keyDescriptionDao
        keyDescriptionDao.insert(keyDescription)
    }

    private fun pickImage() {
        imageGalleryPicker!!.allowMultiple() // Default is false
        imageGalleryPicker!!.setRequestId(ConstantManager.REQUEST_GALLERY_IMAGE_FILE)
        imageGalleryPicker!!.pickImage()
    }

    private fun pickMainImage() {
        imageGalleryPicker!!.allowMultiple()
        imageGalleryPicker!!.setRequestId(ConstantManager.REQUEST_MAIN_IMAGE_FILE)
        imageGalleryPicker!!.pickImage()
    }

    /*
    Hide input method
     */
    private fun packUpImm() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bar_save -> {
                packUpImm()
                if (!DatabaseUtil.updateItemName(this@ItemEditActivity, itemName!!.text.toString(), currentID)) {
                    Toast.makeText(this, R.string.save_item_failed, Toast.LENGTH_LONG).show()
                    return true
                }
                if (!DatabaseUtil.updateDetailDescription(this@ItemEditActivity, detailDescription!!.text.toString(), currentID)) {
                    Toast.makeText(this, R.string.save_item_failed, Toast.LENGTH_LONG).show()
                    return true
                }
                if (!DatabaseUtil.updateItemPrice(this@ItemEditActivity, et_price.text.toString().toFloat(), this.item)) {
                    Toast.makeText(this, R.string.save_item_failed, Toast.LENGTH_LONG).show()
                    return true
                }
                if (!DatabaseUtil.updateItemAvailableInventory(this@ItemEditActivity, et_available_inventory.text.toString().toInt(), this.item)) {
                    Toast.makeText(this, R.string.save_item_failed, Toast.LENGTH_LONG).show()
                    return true
                }
                Toast.makeText(this@ItemEditActivity, R.string.saved_item, Toast.LENGTH_LONG).show()
                val intent = Intent(this, ItemDetailActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_ITEM_ID, currentID)
                startActivity(intent)
                finish()
            }
            android.R.id.home -> {
                val intent1 = Intent(this, ItemDetailActivity::class.java)
                intent1.putExtra(ConstantManager.CURRENT_ITEM_ID, currentID)
                startActivity(intent1)
                finish()
            }
        }
        return true
    }

    /**
     * as name said.
     */
    private fun registNewCardsBroadCast() {
        val filter = IntentFilter(ConstantManager.NEW_RFID_CARD_BROADCAST_ACTION)
        registerReceiver(newCardsReceiver, filter)
    }

    /**
     * Receive notification of scanned cards
     */
    private val newCardsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ConstantManager.NEW_RFID_CARD_BROADCAST_ACTION == action) {
                supportActionBar!!.title = getString(R.string.item_number) +
                        (application as MyApplication).savedCardsNumber
            }
        }
    }
}
