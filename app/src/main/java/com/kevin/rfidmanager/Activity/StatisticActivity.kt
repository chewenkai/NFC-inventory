package com.kevin.rfidmanager.Activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import com.kevin.rfidmanager.Adapter.StatisticListAdapter
import com.kevin.rfidmanager.Bean.SaleStasticInfo
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.TimeUtil
import com.kevin.rfidmanager.database.ItemsDao
import com.kevin.rfidmanager.database.SaleInfoDao
import kotlinx.android.synthetic.main.activity_statistic.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticActivity : AppCompatActivity() {
    var currentUser = ""
    val DAY = "Day"
    val WEEK = "Week"
    val MONTH = "Month"
    val YEAR = "Year"
    val CUSTOM = "Custom"
    var statisticAdapter: StatisticListAdapter? = null
    var startDatePicker: DatePicker? = null
    var endDatePicker: DatePicker? = null

    var isStatisticYear = 0
    var isStatisticMonth = 1
    var isStatisticWeek = 2
    var isStatisticDay = 3
    var selectedButton = isStatisticDay

    private var selected_start_year = ""
    private var selected_start_month = ""
    private var selected_start_day = ""
    private var selected_end_year = ""
    private var selected_end_month = ""
    private var selected_end_day = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        supportActionBar?.title = "Statistic"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initUI()
        refreshData()
    }

    private fun initUI() {
        currentUser = intent.getStringExtra(ConstantManager.CURRENT_USER_NAME)
        tab_layout.addTab(tab_layout.newTab().setText(DAY))
        tab_layout.addTab(tab_layout.newTab().setText(WEEK))
        tab_layout.addTab(tab_layout.newTab().setText(MONTH))
        tab_layout.addTab(tab_layout.newTab().setText(YEAR))
        tab_layout.addTab(tab_layout.newTab().setText(CUSTOM))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    DAY -> {
                        selectedButton = isStatisticDay; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    WEEK -> {
                        selectedButton = isStatisticWeek; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    MONTH -> {
                        selectedButton = isStatisticMonth; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    YEAR -> {
                        selectedButton = isStatisticYear; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    CUSTOM -> {
                        select_date_layout.visibility = View.VISIBLE
                        start_date_picker_layout.removeAllViews()
                        end_date_picker_layout.removeAllViews()
                        initDatePicker()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    DAY -> {
                        selectedButton = isStatisticDay; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    WEEK -> {
                        selectedButton = isStatisticWeek; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    MONTH -> {
                        selectedButton = isStatisticMonth; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    YEAR -> {
                        selectedButton = isStatisticYear; refreshData()
                        select_date_layout.visibility = View.GONE
                    }
                    CUSTOM -> {
                        select_date_layout.visibility = View.VISIBLE
                        start_date_picker_layout.removeAllViews()
                        end_date_picker_layout.removeAllViews()
                        initDatePicker()
                    }
                }
            }

        })

        done_select_date.onClick {
            select_date_layout.visibility = View.GONE
            // get the start time
            val startMills = TimeUtil.getMillisTime("$selected_start_year-$selected_start_month-$selected_start_day",
                    SimpleDateFormat("yyyy-MM-dd"))
            // get the start time
            val endMills = TimeUtil.getMillisTime("$selected_end_year-$selected_end_month-$selected_end_day",
                    SimpleDateFormat("yyyy-MM-dd"))
            if (startMills == null || endMills == null) {
                toast("Please select date.")
                return@onClick
            }
            val saleList = queryStatisticInfo(startMills, endMills)

            statisticAdapter?.updateDate(saleList)
            total_volume.text = "Sold Units:: ${saleList.sumBy { it.volume }}"
            total_price.text = "Product Sales: " + String.format("%.0f", saleList.sumByDouble { it.price.toDouble() })
        }

        // init the list
        statisticAdapter = StatisticListAdapter(this@StatisticActivity, ArrayList<SaleStasticInfo>())
        statistic_list.adapter = statisticAdapter!!
        // set head view
        val v = layoutInflater.inflate(R.layout.statistic_adaper_layout, null)
        (v.findViewById(R.id.item_name) as TextView).text = "Product Name"
        (v.findViewById(R.id.item_name) as TextView).typeface = Typeface.DEFAULT_BOLD
        (v.findViewById(R.id.unit_price) as TextView).text = "Unit Price"
        (v.findViewById(R.id.unit_price) as TextView).typeface = Typeface.DEFAULT_BOLD
        (v.findViewById(R.id.volume) as TextView).text = "Sold Units"
        (v.findViewById(R.id.volume) as TextView).typeface = Typeface.DEFAULT_BOLD
        (v.findViewById(R.id.stock) as TextView).text = "Stock"
        (v.findViewById(R.id.stock) as TextView).typeface = Typeface.DEFAULT_BOLD
        (v.findViewById(R.id.price) as TextView).text = "Product Sales"
        (v.findViewById(R.id.price) as TextView).typeface = Typeface.DEFAULT_BOLD
        statistic_list.addHeaderView(v)
    }

    private fun refreshData() {
        val calendar = Calendar.getInstance()
        var thresholdMills = 0L
        when (selectedButton) {
            isStatisticDay -> {
                thresholdMills = TimeUtil.getMillisTime(calendar.get(Calendar.YEAR).toString() + "-" +
                        (calendar.get(Calendar.MONTH) + 1).toString() + "-" +
                        calendar.get(Calendar.DAY_OF_MONTH).toString(), SimpleDateFormat("yyyy-M-d"))
            }
            isStatisticWeek -> {
                thresholdMills = TimeUtil.getMillisTime(calendar.get(Calendar.YEAR).toString() + "-" +
                        (calendar.get(Calendar.MONTH) + 1).toString() + "-" +
                        (calendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_WEEK)).toString(),
                        SimpleDateFormat("yyyy-M-d"))
            }
            isStatisticMonth -> {
                thresholdMills = TimeUtil.getMillisTime(calendar.get(Calendar.YEAR).toString() + "-" +
                        (calendar.get(Calendar.MONTH) + 1).toString() + "-01", SimpleDateFormat("yyyy-M-d"))

            }
            isStatisticYear -> {
                thresholdMills = TimeUtil.getMillisTime(calendar.get(Calendar.YEAR).toString() + "-01" + "-01",
                        SimpleDateFormat("yyyy-M-d"))
            }
        }

        val saleList = queryStatisticInfo(thresholdMills, System.currentTimeMillis())
        statisticAdapter?.updateDate(saleList)
        total_volume.text = "Sold Units: ${saleList.sumBy { it.volume }}"
        total_price.text = "Product Sales: " + String.format("%.0f", saleList.sumByDouble { it.price.toDouble() })
    }

    fun queryStatisticInfo(startThresholdMills: Long, endThresholdMills: Long): ArrayList<SaleStasticInfo> {
        val saleList = ArrayList<SaleStasticInfo>()
        val allItems = (application as MyApplication).daoSession?.itemsDao?.queryBuilder()?.
                where(ItemsDao.Properties.UserName.eq(currentUser))?.list()
        if (allItems != null) {
            for (item in allItems) {
                val sale = SaleStasticInfo()
                sale.itemName = item.itemName
                sale.unitPrice = item.price
                sale.stock = item.avaliableInventory
                val saleRecords = (application as MyApplication).daoSession?.saleInfoDao?.queryBuilder()?.
                        where(SaleInfoDao.Properties.Rfid.eq(item.rfid), SaleInfoDao.Properties.SaleTime.gt(startThresholdMills),
                                SaleInfoDao.Properties.Rfid.eq(item.rfid), SaleInfoDao.Properties.SaleTime.lt(endThresholdMills))?.list()
                if (saleRecords == null) {
                    sale.volume = 0
                    sale.price = 0f
                } else {
                    sale.volume = saleRecords.sumBy { it.saleVolume }
                    sale.price = saleRecords.sumByDouble { it.price.toDouble() }.toFloat()
                }
                saleList.add(sale)
            }
        } else
            return ArrayList<SaleStasticInfo>()

        saleList.sortByDescending { it.volume }
        return saleList
    }

    fun initDatePicker() {
        startDatePicker = DatePicker(this@StatisticActivity, DatePicker.YEAR_MONTH_DAY)
        startDatePicker?.setLabel(getString(R.string.year_label), getString(R.string.month_label), getString(R.string.day_label))
        endDatePicker = DatePicker(this@StatisticActivity, DatePicker.YEAR_MONTH_DAY)
        endDatePicker?.setLabel(getString(R.string.year_label), getString(R.string.month_label), getString(R.string.day_label))
        startDatePicker!!.setOnWheelListener(object : DatePicker.OnWheelListener {
            override fun onMonthWheeled(index: Int, month: String?) {
                selected_start_month = month ?: ""
            }

            override fun onYearWheeled(index: Int, year: String?) {
                selected_start_year = year ?: ""
            }

            override fun onDayWheeled(index: Int, day: String?) {
                selected_start_day = day ?: ""
            }

        })

        endDatePicker!!.setOnWheelListener(object : DatePicker.OnWheelListener {
            override fun onMonthWheeled(index: Int, month: String?) {
                selected_end_month = month ?: ""
            }

            override fun onYearWheeled(index: Int, year: String?) {
                selected_end_year = year ?: ""
            }

            override fun onDayWheeled(index: Int, day: String?) {
                selected_end_day = day ?: ""
            }

        })

        start_date_picker_layout.addView(startDatePicker?.contentView)
        end_date_picker_layout.addView(endDatePicker?.contentView)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, ItemListActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_USER_NAME, currentUser)
            startActivity(intent)
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
                val intent = Intent(this, ItemListActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_USER_NAME, currentUser)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
