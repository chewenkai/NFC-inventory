package com.kevin.rfidmanager.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import at.markushi.ui.CircleButton
import com.kevin.rfidmanager.Adapter.ItemListAdaper
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.SPUtil
import com.kevin.rfidmanager.database.Items
import kotlinx.android.synthetic.main.item_inventory_list_layout.*

/**
 * List all items in database.
 */
class ItemListActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var itemListAdapter: ItemListAdaper? = null
    val items: MutableList<Items> = ArrayList<Items>()
    private var deleteItemsButton: CircleButton? = null
    var currentUser = ConstantManager.DEFAULT_USER
    var currentID = ConstantManager.DEFAULT_RFID



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_inventory_list_layout)
        initActionBar()
        initUI()
//        registNewCardsBroadCast()
    }

    private fun initActionBar() {
        val actionBar = supportActionBar!!
        actionBar.title = getString(R.string.all_items_actionbar_title)
        actionBar.setHomeButtonEnabled(true)
    }

    private fun initUI() {
        currentUser = intent.getStringExtra(ConstantManager.CURRENT_USER_NAME)
        recyclerView = findViewById(R.id.recycle_item_list) as RecyclerView
        deleteItemsButton = delete_items_button
        emptyHint.text = getString(R.string.no_items_recorded)
        itemListAdapter = ItemListAdaper(this@ItemListActivity, items, recyclerView,
                deleteItemsButton!!, emptyHint, true)
        recyclerView!!.adapter = itemListAdapter
        setRecyclerViewLayout()
        recyclerView!!.setHasFixedSize(true)
        deleteItemsButton!!.setOnClickListener { itemListAdapter!!.deleteSelectedItems() }

    }

    public override fun onResume() {
        super.onResume()
        if (itemListAdapter != null) {
            itemListAdapter!!.updateUI()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
//        unregisterReceiver(newCardsReceiver)
        super.onDestroy()
    }

    private fun setRecyclerViewLayout() {
        when (SPUtil.getInstence(this@ItemListActivity).apperance) {
            8  // ConstantManager.LINEAR_LAYOUT
            -> {
                val gridLayoutManager = GridLayoutManager(this@ItemListActivity,
                        3, GridLayoutManager.VERTICAL, false)
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
                        this@ItemListActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView!!.layoutManager = linearLayoutManager
            }
            11 -> {
                val linearLayoutManager = LinearLayoutManager(
                        this@ItemListActivity, LinearLayoutManager.VERTICAL, false)
                recyclerView!!.layoutManager = linearLayoutManager
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.empty_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
