package com.kevin.rfidmanager.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import com.kevin.rfidmanager.Adapter.CheckoutAdaper
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.database.SaleInfo
import kotlinx.android.synthetic.main.activity_checkout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

class CheckoutActivity : AppCompatActivity() {
    var checkItems: ArrayList<CheckoutAdaper.ItemWithCount>? = null
    var itemID = ArrayList<String>()
    var count = ArrayList<String>()
    var invoice = ""
    var totalPrice = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        supportActionBar?.title = "Checkout"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkItems = intent.getParcelableArrayListExtra(ConstantManager.CHECKOUT_ITEMS)
        itemID = intent.getStringArrayListExtra(ConstantManager.CHECKOUT_ITEMS_ID)
        count = intent.getStringArrayListExtra(ConstantManager.CHECKOUT_ITEMS_COUNT_EXTRA)
        for (i in checkItems!!.indices) {
            checkItems?.get(i)?.item = DatabaseUtil.queryItemsById(this, itemID.get(i))
            val price = checkItems?.get(i)?.item?.price!! * checkItems?.get(i)?.count!!
            totalPrice += price * count?.get(i)?.toInt()
            invoice += "Item: " + checkItems?.get(i)?.item?.itemName + "\t Amount: \t" +
                    count?.get(i) + "\t Price: " + String.format("%.0f", price) + "\n------------------------\n"
        }
        invoice += "\n\n    Total:" + String.format("%.0f", totalPrice) + "\n\n"

        invoice_textview.text = invoice

        credit_pay.onClick {
            paid()
        }

        cash_pay.onClick {
            paid()
        }

    }

    fun paid() {
        if (first_name.text.isNotEmpty() && first_name.text.isNotBlank() &&
                last_name.text.isNotEmpty() && last_name.text.isNotBlank() &&
                company_name.text.isNotEmpty() && company_name.text.isNotBlank() &&
                phone_number.text.isNotEmpty() && phone_number.text.isNotBlank() &&
                address.text.isNotEmpty() && address.text.isNotBlank() &&
                email_address.text.isNotEmpty() && email_address.text.isNotBlank()) {
            DatabaseUtil.updateMultiItems(this@CheckoutActivity, checkItems)
            for (item in checkItems!!) {
                if (item.count == 0)
                    continue
                val saleInfo = SaleInfo(null, item.item?.userName, item.item?.rfid, item.item?.itemName,
                        item.item?.price!! * item.count, item.item?.mainImagePath, item.item?.detailDescription, System
                        .currentTimeMillis(), item.count)
                (application as MyApplication).daoSession?.saleInfoDao?.insertOrReplace(saleInfo)
            }
            toast(getString(R.string.checkout_successful))
            val intent = Intent().setAction(ConstantManager.PAY_SUCCESSFUL_BROADCAST)
            intent.putExtra(ConstantManager.PAY_SUCCESSFUL, true)
            sendBroadcast(intent)
            finish()
        } else {
            toast("Please input your information firstly")
            return
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toast("Checkout canceled")
            finish()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                toast("Checkout canceled")
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
