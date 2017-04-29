package com.kevin.rfidmanager.Activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.kevin.rfidmanager.Adapter.GallaryAdaper
import com.kevin.rfidmanager.Adapter.KeyDesListAdapter
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.ScreenUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_add_layout.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor
import java.io.File

class ItemDetailActivity : AppCompatActivity() {
    private var textViewItemName: TextView? = null
    private var addKeyDes: TextView? = null
    private var detailDescriptionTitle: TextView? = null
    private var key_des_list: ListView? = null
    private var mainImage: ImageView? = null
    private var addGalleryButton: AppCompatButton? = null
    private var detailDescription: EditText? = null
    private var itemName: EditText? = null

    private var recyclerView: RecyclerView? = null
    private var gallaryAdaper: GallaryAdaper? = null
    private var desListAdapter: KeyDesListAdapter? = null
    var currentID = ConstantManager.DEFAULT_RFID

    private val hideEditButtons = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_add_layout)
        val actionBar = supportActionBar!!
        actionBar.setTitle(R.string.detail_page)
        actionBar.setHomeButtonEnabled(true)
        initUI()
        registNewCardsBroadCast()
    }

    override fun onResume() {
        initUI()
        super.onResume()
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
        val item = DatabaseUtil.getCurrentItem(this, currentID)
        if (item == null) {
            Toast.makeText(this, R.string.item_not_exist, Toast.LENGTH_LONG).show()
            return
        }

        itemName = findViewById(R.id.item_name) as EditText
        itemName!!.visibility = View.GONE

        textViewItemName = findViewById(R.id.textview_item_name) as TextView
        textViewItemName!!.text = item.itemName
        textViewItemName!!.visibility = View.VISIBLE

        key_des_list = findViewById(R.id.listview_item_key_des) as ListView
        desListAdapter = KeyDesListAdapter(this@ItemDetailActivity,
                DatabaseUtil.queryItemsKeyDes(this@ItemDetailActivity, currentID), hideEditButtons,
                currentID)
        key_des_list!!.adapter = desListAdapter
        desListAdapter!!.setCurrentActivity(this@ItemDetailActivity)

        mainImage = findViewById(R.id.iamgeview_main_image) as ImageView
        val mainImagePath = item.mainImagePath
        if (mainImagePath != null) {
            if (ContextCompat.checkSelfPermission(this@ItemDetailActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // check if the file exist
                if (File(mainImagePath).exists()) {
                    Picasso.with(this@ItemDetailActivity).load(File(mainImagePath)).resize(ScreenUtil.getScreenWidth(this@ItemDetailActivity) / 2, 0).into(mainImage)
                } else {
                    Picasso.with(this@ItemDetailActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemDetailActivity) / 2, 0).into(mainImage)
                }
                Picasso.with(this@ItemDetailActivity).load(File(mainImagePath)).resize(ScreenUtil.getScreenWidth(this@ItemDetailActivity) / 2, 0).into(mainImage)
                mainImage!!.setOnClickListener {
                    // Open picture
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    intent.setDataAndType(FileProvider.getUriForFile(this@ItemDetailActivity,
                            applicationContext.packageName + ".provider", File(mainImagePath)), "image/*")
                    startActivity(intent)
                }
            } else {
                Picasso.with(this@ItemDetailActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemDetailActivity) / 2, 0).into(mainImage)
                mainImage!!.setOnClickListener {
                    // Disable Click Listening
                }
            }
        } else {
            Picasso.with(this@ItemDetailActivity).load(R.drawable.image_read_fail).resize(ScreenUtil.getScreenWidth(this@ItemDetailActivity) / 2, 0).into(mainImage)
            mainImage!!.setOnClickListener {
                // Disable Click Listening
            }
        }


        addKeyDes = findViewById(R.id.button_add_item_key_des) as TextView
        addKeyDes!!.visibility = View.GONE

        et_available_inventory.enabled = false
        et_available_inventory.textColor = resources.getColor(R.color.black)
        et_available_inventory.setText(item!!.avaliableInventory.toString())

        et_price.enabled = false
        et_price.textColor = resources.getColor(R.color.black)
        et_price.setBackgroundColor(resources.getColor(R.color.white))
        et_price.setText((item.price.toInt()).toString())

        detailDescriptionTitle = findViewById(R.id.detail_description_title) as TextView
        detailDescriptionTitle!!.visibility = View.GONE

        addGalleryButton = findViewById(R.id.add_gallery_image) as AppCompatButton
        addGalleryButton!!.visibility = View.GONE

        detailDescription = findViewById(R.id.detail_description) as EditText
        detailDescription!!.setText(item.detailDescription)
        detailDescription!!.isEnabled = false
        detailDescription!!.setBackgroundColor(resources.getColor(R.color.white))
        detailDescription!!.setTextColor(resources.getColor(R.color.black))

        recyclerView = findViewById(R.id.recycle_gallery) as RecyclerView
        gallaryAdaper = GallaryAdaper(this@ItemDetailActivity, DatabaseUtil.queryImagesPaths(this@ItemDetailActivity, currentID), hideEditButtons, currentID)
        recyclerView!!.adapter = gallaryAdaper
        val layoutManager = LinearLayoutManager(this@ItemDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.scrollToPosition(0)// Optionally customize the position you want to default scroll to
        recyclerView!!.layoutManager = layoutManager// Attach layout manager to the RecyclerView
        //        StaggeredGridLayoutManager gridLayoutManager =
        //                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
        //        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView!!.setHasFixedSize(true)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bar_edit -> {
                val intent = Intent(this, ItemEditActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_ITEM_ID, currentID)
                startActivity(intent)
                finish()
            }
            android.R.id.home -> finish()
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
