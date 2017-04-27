package com.kevin.rfidmanager.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AnReaderDemo.InventoryAdapter;
import com.example.AnReaderDemo.InventoryReport;
import com.example.AnReaderDemo.ScanAdapter;
import com.example.AnReaderDemo.ScanReport;
import com.kevin.rfidmanager.R;
import com.rfid.api.ADReaderInterface;
import com.rfid.api.BluetoothCfg;
import com.rfid.api.GFunction;
import com.rfid.api.ISO15693Interface;
import com.rfid.api.ISO15693Tag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RFIDListActivity extends Activity implements OnClickListener {
    private static final int INVENTORY_MSG = 1;
    private static final int GETSCANRECORD = 2;
    private static final int INVENTORY_FAIL_MSG = 4;
    private static final int THREAD_END = 3;
    private TabHost myTabhost = null;


    private Button btn_connect = null;// connect tag
    private Button btn_disconnect = null;// disconnect
    private Button btn_getDevInfo = null;// get device information
    private Button btn_setTime = null;// set time
    private Button btn_startInventory = null;// start inventory
    private Button btn_stopInventory = null;// stop inventory
    private Button btn_setInventoryPara = null;// set para of inventory
    private Button btn_startScanf = null;// Start scanf
    private Button btn_stopScanf = null;// stop scanf
    private Button btn_openRF = null;// Open RF
    private Button btn_closeRF = null;// Close RF
    private Button btn_clearInventoryRecord = null;// Clear inventory list
    private Button btn_clearScanfRecordList = null;// clear scanf record
    private Spinner sn_RfPower = null;// RF Power
    private Button btn_readPower = null;// Get RF Power
    private Button btn_setPower = null;// Set RF Power
    private Button btn_loadDefault = null;// Reset
    private Spinner sn_overflow_time = null;// overflow_time
    private Button btn_read_overflow_time = null;// get overflow_time
    private Button btn_write_overflow_time = null;// Set overflow_time

    private ListView list_inventory_record = null;// inventory list
    private ListView list_scanf_record = null;// scanf record list
    private ListView list_tag_name = null;// tag name

    private TextView tv_inventoryInfo = null;
    private TextView tv_scanRecordInfo = null;
    private List<InventoryReport> inventoryList = new ArrayList<InventoryReport>();
    private List<ScanReport> scanfReportList = new ArrayList<ScanReport>();
    private InventoryAdapter inventoryAdapter = null;
    private ScanAdapter scanfAdapter = null;
    ;
    static ADReaderInterface m_reader = new ADReaderInterface();

    // Inventory parameter
    static int INVENTORY_REQUEST_CODE = 1;// requestCode
    private boolean bUseDefaultPara = true;
    private boolean bOnlyReadNew = false;
    private boolean bMathAFI = false;
    private byte mAFIVal = 0x00;
    private boolean bBuzzer = true;

    private SoundPool soundPool = null;
    private int soundID = 0;

    private Thread m_inventoryThrd = null;// The thread of inventory
    private Thread m_getScanRecordThrd = null;// The thead of scanf the record.
    // Only for rpan device.

    private boolean isLoadScanfMode = false;

    private int[] layRes = {R.id.tab_reader, R.id.tab_command,
            R.id.tab_inventory, R.id.tab_TagTypeList, R.id.tab_ScanRecord};
    private String[] layTittle = null; // { "设备", "命令", "盘点", "读写", "扫描" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rfid_list_acitivty);

        layTittle = new String[]{getString(R.string.tx_tab_device),
                getString(R.string.tx_tab_command),
                getString(R.string.tx_tab_inventory),
                getString(R.string.tx_tab_operate),
                getString(R.string.tx_tab_scanf)};

        myTabhost = (TabHost) findViewById(R.id.tabhost);
        list_inventory_record = (ListView) findViewById(R.id.list_inventory_record);
        list_scanf_record = (ListView) findViewById(R.id.list_scanf_record);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_getDevInfo = (Button) findViewById(R.id.btn_getDevInfo);
        btn_setTime = (Button) findViewById(R.id.btn_setTime);
        btn_startInventory = (Button) findViewById(R.id.btn_startInventory);
        btn_stopInventory = (Button) findViewById(R.id.btn_stopInventory);
        btn_setInventoryPara = (Button) findViewById(R.id.btn_paraInventory);
        btn_startScanf = (Button) findViewById(R.id.btn_startScanfRecord);
        btn_stopScanf = (Button) findViewById(R.id.btn_stopScanfRecord);
        btn_openRF = (Button) findViewById(R.id.btn_openRF);
        btn_closeRF = (Button) findViewById(R.id.btn_closeRF);
        sn_RfPower = (Spinner) findViewById(R.id.sn_powerVal);
        btn_readPower = (Button) findViewById(R.id.btn_readPower);
        btn_setPower = (Button) findViewById(R.id.btn_setPower);
        btn_clearInventoryRecord = (Button) findViewById(R.id.btn_clearList);
        btn_clearScanfRecordList = (Button) findViewById(R.id.btn_clearScanfRecordList);
        btn_loadDefault = (Button) findViewById(R.id.btn_loadDefault);
        tv_inventoryInfo = (TextView) findViewById(R.id.tv_inventoryInfo);
        tv_scanRecordInfo = (TextView) findViewById(R.id.tv_scanRecordInfo);
        list_tag_name = (ListView) findViewById(R.id.list_tagName);
        sn_overflow_time = (Spinner) findViewById(R.id.sn_overflow_time);
        btn_read_overflow_time = (Button) findViewById(R.id.btn_read_overflow_time);
        btn_write_overflow_time = (Button) findViewById(R.id.btn_write_overflow_time);

        // Load page
        myTabhost.setup();
        for (int i = 0; i < layRes.length - 1; i++) {
            TabSpec myTab = myTabhost.newTabSpec("tab" + i);
            myTab.setIndicator(layTittle[i]);
            myTab.setContent(layRes[i]);
            myTabhost.addTab(myTab);
        }
        myTabhost.setCurrentTab(0);

        // Inventory list tittle
        ViewGroup InventorytableTitle = (ViewGroup) findViewById(R.id.inventorylist_title);
        InventorytableTitle.setBackgroundColor(Color.rgb(255, 100, 10));

        // Scanf list tittle
        ViewGroup ScanRecordTableTitle = (ViewGroup) findViewById(R.id.scan_record_list_tittle);
        ScanRecordTableTitle.setBackgroundColor(Color.rgb(53, 190, 106));

        scanfAdapter = new ScanAdapter(this, scanfReportList);
        list_scanf_record.setAdapter(scanfAdapter);

        inventoryAdapter = new InventoryAdapter(this, inventoryList);
        list_inventory_record.setAdapter(inventoryAdapter);

        final String[] tagName = new String[]{"ICODE_SLI"};
        List<Map<String, Object>> tagNameListItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < tagName.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>(); // 实例化Map对象
            map.put("image", R.drawable.arrow);
            map.put("title", tagName[i]);
            tagNameListItems.add(map); // 将map对象添加到List集合中
        }

        // 读写选项标签列表
        SimpleAdapter tagNamadapter = new SimpleAdapter(this, tagNameListItems,
                R.xml.tag_name_items, new String[]{"title", "image"},
                new int[]{R.id.tagListtitle, R.id.tagListimage}); // 创建SimpleAdapter
        list_tag_name.setAdapter(tagNamadapter);
        list_tag_name.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case 0:
                        // ICODE_SLI TAG
//                        Intent intent = new Intent(RFIDListActivity.this,
//                                IcodesliTagActivity.class);
//                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        // 列举已配对的蓝牙设备
        // Get the bluetooth
        ArrayList<CharSequence> m_bluetoolNameList = null;
        ArrayAdapter<CharSequence> m_adaBluetoolName = null;
        m_bluetoolNameList = new ArrayList<CharSequence>();
        ArrayList<BluetoothCfg> m_blueList = ADReaderInterface
                .GetPairBluetooth();
        if (m_blueList != null) {
            for (BluetoothCfg bluetoolCfg : m_blueList) {
                m_bluetoolNameList.add(bluetoolCfg.GetName());
            }
        }

        ArrayList<CharSequence> overflowTime = new ArrayList<CharSequence>();
        for (int i = 0; i < 256; i++) {
            overflowTime.add(i + "");
        }
        ArrayAdapter<CharSequence> overflowTimeAda = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_dropdown_item,
                overflowTime);
        sn_overflow_time.setAdapter(overflowTimeAda);

        btn_connect.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        btn_getDevInfo.setOnClickListener(this);
        btn_setTime.setOnClickListener(this);
        btn_openRF.setOnClickListener(this);
        btn_closeRF.setOnClickListener(this);
        btn_startInventory.setOnClickListener(this);
        btn_stopInventory.setOnClickListener(this);
        btn_clearInventoryRecord.setOnClickListener(this);
        btn_startScanf.setOnClickListener(this);
        btn_stopScanf.setOnClickListener(this);
        btn_clearScanfRecordList.setOnClickListener(this);
        btn_setPower.setOnClickListener(this);
        btn_readPower.setOnClickListener(this);
        btn_loadDefault.setOnClickListener(this);
        btn_setInventoryPara.setOnClickListener(this);
        btn_read_overflow_time.setOnClickListener(this);
        btn_write_overflow_time.setOnClickListener(this);

        btn_connect.setEnabled(true);
        btn_disconnect.setEnabled(false);
        btn_getDevInfo.setEnabled(false);
        btn_setTime.setEnabled(false);
        btn_openRF.setEnabled(false);
        btn_closeRF.setEnabled(false);
        btn_startInventory.setEnabled(false);
        btn_stopInventory.setEnabled(false);
        btn_setInventoryPara.setEnabled(false);
        btn_startScanf.setEnabled(false);
        btn_stopScanf.setEnabled(false);
        sn_RfPower.setEnabled(false);
        btn_readPower.setEnabled(false);
        btn_setPower.setEnabled(false);
        btn_loadDefault.setEnabled(false);
        list_tag_name.setEnabled(false);
        sn_overflow_time.setEnabled(false);
        btn_write_overflow_time.setEnabled(false);
        btn_read_overflow_time.setEnabled(false);

        // 初始化声音
        // Initialize the sound
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
        soundID = soundPool.load(this, R.raw.msg, 1);

    }

    @Override
    protected void onDestroy() {
        soundPool.unload(soundID);
        if (m_reader.isReaderOpen()) {
            // 如果盘点标签线程正在运行，则关闭该线程
            // If thread of inventory is running,stop the thread before exit the
            // application.
            if (m_inventoryThrd != null && m_inventoryThrd.isAlive()) {
                b_inventoryThreadRun = false;
                m_reader.RDR_SetCommuImmeTimeout();
                try {
                    m_inventoryThrd.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 如果获取扫描记录线程正在运行，则关闭该线程
            // If thread of scannig is running,stop the thread before exit the
            // application.
            if (m_getScanRecordThrd != null && m_getScanRecordThrd.isAlive()) {
                bGetScanRecordFlg = false;
                m_reader.RDR_SetCommuImmeTimeout();
                try {
                    m_getScanRecordThrd.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CloseDev();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int nret = -1;
        String str = "";
        switch (v.getId()) {
            case R.id.btn_connect:// connect the device
                OpenDev();
                break;
            case R.id.btn_disconnect:// disconnect the device
                CloseDev();
                break;
            case R.id.btn_getDevInfo:// get the informatin of the device
                GetInformation();
                break;
            case R.id.btn_setTime:// Set the time of the device.Only for device of
                // rpan.
                SetSysTime();
                break;
            case R.id.btn_openRF:// Open RF
                nret = m_reader.RDR_OpenRFTransmitter();
                if (nret == ApiErrDefinition.NO_ERROR) {
                    str = getString(R.string.tx_openRF_ok);
                } else {
                    str = getString(R.string.tx_openRF_fail);
                }
                MessageBox(getString(R.string.tx_openRF), str);
                break;
            case R.id.btn_setPower:// Set the power
                byte powerIndex = (byte) (sn_RfPower.getSelectedItemPosition() + 1);
                nret = m_reader.RDR_SetRFPower(powerIndex);
                if (nret == ApiErrDefinition.NO_ERROR) {
                    str = getString(R.string.tx_setPower_ok);
                } else {
                    str = getString(R.string.tx_setPower_fail);
                }
                MessageBox(getString(R.string.tx_setPower), str);
                break;
            case R.id.btn_loadDefault:// reset
                nret = m_reader.RDR_LoadFactoryDefault();
                if (nret == ApiErrDefinition.NO_ERROR) {
                    str = getString(R.string.tx_loadDefault_ok);
                } else {
                    str = getString(R.string.tx_loadDefault_fail);
                }
                MessageBox(getString(R.string.tx_loadDefault), str);
                break;
            case R.id.btn_readPower:// Get RF Power
                Byte mPower = new Byte((byte) 0);
                nret = m_reader.RDR_GetRFPower(mPower);
                if (nret != ApiErrDefinition.NO_ERROR) {
                    MessageBox(getString(R.string.tx_getRFPower),
                            getString(R.string.tx_getRFPower_fail) + nret);
                    break;
                }
                sn_RfPower.setSelection(mPower.byteValue() - 1);
                MessageBox(getString(R.string.tx_getRFPower),
                        getString(R.string.tx_getRFPower_ok));
                break;
            case R.id.btn_closeRF:// 关闭射频
                nret = m_reader.RDR_CloseRFTransmitter();
                if (nret == ApiErrDefinition.NO_ERROR) {
                    str = getString(R.string.tx_CloseRF_ok);// "关闭射频成功！";
                } else {
                    str = getString(R.string.tx_CloseRF_fail);// "关闭射频失败";
                }
                MessageBox(getString(R.string.tx_CloseRF), str);
                break;
            case R.id.btn_startInventory:// 开始盘点
                btn_connect.setEnabled(false);
                btn_disconnect.setEnabled(false);
                btn_getDevInfo.setEnabled(false);
                btn_setTime.setEnabled(false);
                btn_openRF.setEnabled(false);
                btn_closeRF.setEnabled(false);
                btn_startInventory.setEnabled(false);
                btn_stopInventory.setEnabled(true);
                btn_setInventoryPara.setEnabled(false);
                btn_clearInventoryRecord.setEnabled(false);
                btn_startScanf.setEnabled(false);
                btn_stopScanf.setEnabled(false);
                sn_RfPower.setEnabled(false);
                btn_readPower.setEnabled(false);
                btn_setPower.setEnabled(false);
                btn_loadDefault.setEnabled(false);
                list_tag_name.setEnabled(false);
                sn_overflow_time.setEnabled(false);
                btn_read_overflow_time.setEnabled(false);
                btn_write_overflow_time.setEnabled(false);
                inventoryList.clear();
                inventoryAdapter.notifyDataSetChanged();
                tv_inventoryInfo.setText(getString(R.string.tx_inventory_sum0));
                m_inventoryThrd = new Thread(new InventoryThrd());
                m_inventoryThrd.start();
                break;
            case R.id.btn_stopInventory:// 停止盘点
                btn_stopInventory.setEnabled(false);
                m_reader.RDR_SetCommuImmeTimeout();
                b_inventoryThreadRun = false;
                break;
            case R.id.btn_paraInventory:// 盘点参数设置
//                Intent intent = new Intent(this, InventoryParaActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("UseDefaultPara", this.bUseDefaultPara);
//                bundle.putBoolean("OnlyReadNew", this.bOnlyReadNew);
//                bundle.putBoolean("MathAFI", this.bMathAFI);
//                bundle.putByte("AFI", this.mAFIVal);
//                bundle.putBoolean("bBuzzer", this.bBuzzer);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, INVENTORY_REQUEST_CODE);
                break;
            case R.id.btn_clearList:// 清空列表
                inventoryList.clear();
                this.inventoryAdapter.notifyDataSetChanged();
                tv_inventoryInfo.setText(getString(R.string.tx_inventory_sum0));
                break;
            case R.id.btn_startScanfRecord:// 开始扫描
                btn_connect.setEnabled(false);
                btn_disconnect.setEnabled(false);
                btn_getDevInfo.setEnabled(false);
                btn_setTime.setEnabled(false);
                btn_openRF.setEnabled(false);
                btn_closeRF.setEnabled(false);
                btn_startInventory.setEnabled(false);
                btn_stopInventory.setEnabled(false);
                btn_setInventoryPara.setEnabled(false);
                btn_clearInventoryRecord.setEnabled(false);
                btn_startScanf.setEnabled(false);
                btn_stopScanf.setEnabled(true);
                btn_clearScanfRecordList.setEnabled(false);
                sn_RfPower.setEnabled(false);
                btn_readPower.setEnabled(false);
                btn_setPower.setEnabled(false);
                btn_loadDefault.setEnabled(false);
                list_tag_name.setEnabled(false);
                sn_overflow_time.setEnabled(false);
                btn_read_overflow_time.setEnabled(false);
                btn_write_overflow_time.setEnabled(false);
                scanfReportList.clear();
                scanfAdapter.notifyDataSetChanged();
                tv_scanRecordInfo.setText(getString(R.string.tx_scanf_sum0));
                m_getScanRecordThrd = new Thread(new GetScanRecordThrd());
                m_getScanRecordThrd.start();
                break;
            case R.id.btn_stopScanfRecord:// 停止采集记录
                btn_stopScanf.setEnabled(false);
                bGetScanRecordFlg = false;
                break;
            case R.id.btn_clearScanfRecordList:// 清空扫描记录
                scanfReportList.clear();
                tv_scanRecordInfo.setText(getString(R.string.tx_scanf_sum0));
                this.scanfAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_read_overflow_time:// 获取溢出时间
                Integer mTime = 0;
                nret = m_reader.RDR_GetOverflowTime(mTime);
                if (nret != ApiErrDefinition.NO_ERROR) {
                    MessageBox(getString(R.string.tx_getOverflowTime),
                            getString(R.string.tx_getOverflowTime_fail) + nret);
                    break;
                }
                sn_overflow_time.setSelection(mTime.intValue());
                MessageBox(getString(R.string.tx_getOverflowTime),
                        getString(R.string.tx_getOverflowTime_ok));
                break;
            case R.id.btn_write_overflow_time:// 设置溢出时间
                nret = m_reader.RDR_SetOverflowTime(sn_overflow_time
                        .getSelectedItemPosition());
                if (nret != ApiErrDefinition.NO_ERROR) {
                    MessageBox(getString(R.string.tx_setOverflowTime),
                            getString(R.string.tx_setOverflowTime_fail) + nret);
                    break;
                }
                MessageBox(getString(R.string.tx_setOverflowTime),
                        getString(R.string.tx_setOverflowTime_ok) + nret);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (requestCode != INVENTORY_REQUEST_CODE) {
            return;
        }
//        if (resultCode != InventoryParaActivity.RESULT_OK)
//        {
//            return;
//        }
        Bundle bundle = data.getExtras();
        bUseDefaultPara = bundle.getBoolean("UseDefaultPara");
        bOnlyReadNew = bundle.getBoolean("OnlyReadNew");
        bMathAFI = bundle.getBoolean("MathAFI");
        mAFIVal = bundle.getByte("AFI");
        bBuzzer = bundle.getBoolean("bBuzzer");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void MessageBox(String sTittle, String msg) {
        new AlertDialog.Builder(this).setTitle(sTittle).setMessage(msg)
                .setPositiveButton(getString(R.string.tx_msg_certain), null)
                .show();
    }

    /**
     * 打开设备
     */
    private void OpenDev() {
        String conStr = "";
        String commTypeStr;
        String devName = "";
        devName = "RPAN";
        commTypeStr = "USB";

        if (commTypeStr.equals("USB")) {
            // 注意：使用USB方式时，必须先要枚举所有USB设备
            // Note: Before using USB, you must enumerate all USB devices first.
            int usbCnt = ADReaderInterface.EnumerateUsb(this);
            if (usbCnt <= 0) {
                Toast.makeText(this, getString(R.string.tx_msg_noUsb),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ADReaderInterface.HasUsbPermission("")) {
                Toast.makeText(this,
                        getString(R.string.tx_msg_noUsbPermission),
                        Toast.LENGTH_SHORT).show();
                ADReaderInterface.RequestUsbPermission("");
                return;
            }

            conStr = String.format("RDType=%s;CommType=USB;Description=",
                    devName);
        } else {
            return;
        }
        if (m_reader.RDR_Open(conStr) == ApiErrDefinition.NO_ERROR) {
            // ///////////////////////只有RPAN设备支持扫描模式/////////////////////////////
            if (!isLoadScanfMode && devName.equals("RPAN")) {
                findViewById(layRes[4]).setVisibility(View.VISIBLE);
                TabSpec myTab = myTabhost.newTabSpec("tab" + 4);
                myTab.setIndicator(layTittle[4]);
                myTab.setContent(layRes[4]);
                myTabhost.addTab(myTab);
                isLoadScanfMode = true;
            }

            // ///////////////////////////////////////////////////
            Toast.makeText(this, getString(R.string.tx_msg_openDev_ok),
                    Toast.LENGTH_SHORT).show();
            SaveActivity();
            btn_connect.setEnabled(false);
            btn_disconnect.setEnabled(true);
            btn_getDevInfo.setEnabled(true);
            btn_setTime.setEnabled(true);
            btn_openRF.setEnabled(true);
            btn_closeRF.setEnabled(true);
            btn_startInventory.setEnabled(true);
            btn_stopInventory.setEnabled(false);
            btn_setInventoryPara.setEnabled(true);
            btn_clearInventoryRecord.setEnabled(true);
            btn_startScanf.setEnabled(true);
            btn_stopScanf.setEnabled(false);
            sn_RfPower.setEnabled(true);
            btn_readPower.setEnabled(true);
            btn_setPower.setEnabled(true);
            btn_loadDefault.setEnabled(true);
            list_tag_name.setEnabled(true);
            sn_overflow_time.setEnabled(true);
            btn_write_overflow_time.setEnabled(true);
            btn_read_overflow_time.setEnabled(true);
        } else {
            Toast.makeText(this, getString(R.string.tx_msg_openDev_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 播放声音池声音
    private void playVoice() {
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        float audioCurrentVolume = am
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        soundPool.play(soundID, // 播放的音乐Id
                audioCurrentVolume, // 左声道音量
                audioCurrentVolume, // 右声道音量
                1, // 优先级，0为最低
                0, // 循环次数，0无不循环，-1无永远循环
                1);// 回放速度，值在0.5-2.0之间，1为正常速度
    }

    private void CloseDev() {
        if (m_inventoryThrd != null && m_inventoryThrd.isAlive()) {
            MessageBox("", getString(R.string.tx_msg_stopInventory_tip));
            return;
        }
        if (m_getScanRecordThrd != null && m_getScanRecordThrd.isAlive()) {
            MessageBox("", getString(R.string.tx_msg_stopScanf_tip));
            return;
        }
        m_reader.RDR_Close();

        btn_connect.setEnabled(true);
        btn_disconnect.setEnabled(false);
        btn_getDevInfo.setEnabled(false);
        btn_setTime.setEnabled(false);
        btn_openRF.setEnabled(false);
        btn_closeRF.setEnabled(false);
        btn_startInventory.setEnabled(false);
        btn_stopInventory.setEnabled(false);
        btn_setInventoryPara.setEnabled(false);
        btn_startScanf.setEnabled(false);
        btn_stopScanf.setEnabled(false);
        sn_RfPower.setEnabled(false);
        btn_readPower.setEnabled(false);
        btn_setPower.setEnabled(false);
        btn_loadDefault.setEnabled(false);
        list_tag_name.setEnabled(false);
    }

    private void GetInformation() {
        int iret = -1;
        StringBuffer buffer = new StringBuffer();
        iret = m_reader.RDR_GetReaderInfor(buffer);
        if (iret == ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tx_msg_getDevInfo))
                    .setMessage(buffer.toString())
                    .setPositiveButton(getString(R.string.tx_msg_certain), null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tx_msg_getDevInfo))
                    .setMessage(
                            getString(R.string.tx_msg_getDevInfo_fail) + iret)
                    .setPositiveButton(getString(R.string.tx_msg_certain), null)
                    .show();
        }
    }

    private void SetSysTime() {
        int iret = -1;
        Time t = new Time();
        t.setToNow();
        iret = m_reader.RPAN_SetTime(t.year, t.month, t.monthDay, t.hour,
                t.minute, t.second);
        if (iret == ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tx_msg_setTime))
                    .setMessage(getString(R.string.tx_msg_setTime_ok))
                    .setPositiveButton(getString(R.string.tx_msg_certain), null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tx_msg_setTime))
                    .setMessage(getString(R.string.tx_msg_setTime_fail) + iret)
                    .setPositiveButton(getString(R.string.tx_msg_certain), null)
                    .show();
        }
    }

    private void FinishInventory() {
        btn_connect.setEnabled(false);
        btn_disconnect.setEnabled(true);
        btn_getDevInfo.setEnabled(true);
        btn_setTime.setEnabled(true);
        btn_openRF.setEnabled(true);
        btn_closeRF.setEnabled(true);
        btn_startInventory.setEnabled(true);
        btn_stopInventory.setEnabled(false);
        btn_setInventoryPara.setEnabled(true);
        btn_clearInventoryRecord.setEnabled(true);
        btn_startScanf.setEnabled(true);
        btn_stopScanf.setEnabled(false);
        btn_clearScanfRecordList.setEnabled(true);
        sn_RfPower.setEnabled(true);
        btn_readPower.setEnabled(true);
        btn_setPower.setEnabled(true);
        btn_loadDefault.setEnabled(true);
        list_tag_name.setEnabled(true);
        sn_overflow_time.setEnabled(true);
        btn_read_overflow_time.setEnabled(true);
        btn_write_overflow_time.setEnabled(true);
    }

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<RFIDListActivity> mActivity;

        public MyHandler(RFIDListActivity activity) {
            mActivity = new WeakReference<RFIDListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RFIDListActivity pt = mActivity.get();
            if (pt == null) {
                return;
            }
            boolean b_find = false;
            switch (msg.what) {
                case INVENTORY_MSG:// 盘点到标签
                    @SuppressWarnings("unchecked")
                    Vector<ISO15693Tag> tagList = (Vector<ISO15693Tag>) msg.obj;
                    for (int i = 0; i < tagList.size(); i++) {
                        b_find = false;
                        ISO15693Tag tagData = tagList.get(i);// (ISO15693Tag)
                        // msg.obj;
                        String uidStr = GFunction.encodeHexStr(tagData.uid);
                        for (int j = 0; j < pt.inventoryList.size(); j++) {
                            InventoryReport mReport = pt.inventoryList.get(j);
                            if (mReport.getUidStr().equals(uidStr)) {
                                mReport.setFindCnt(mReport.getFindCnt() + 1);
                                b_find = true;
                                break;
                            }
                        }
                        if (!b_find) {
                            String tagName = ISO15693Interface
                                    .GetTagNameById(tagData.tag_id);
                            pt.inventoryList.add(new InventoryReport(uidStr,
                                    tagName));

                        }
                    }
                    pt.tv_inventoryInfo.setText(pt
                            .getString(R.string.tx_info_tagCnt)
                            + pt.inventoryList.size()
                            + pt.getString(R.string.tx_info_failCnt) + msg.arg1);
                    pt.inventoryAdapter.notifyDataSetChanged();
                    break;
                case INVENTORY_FAIL_MSG:
                    pt.tv_inventoryInfo.setText(pt
                            .getString(R.string.tx_info_tagCnt)
                            + pt.inventoryList.size()
                            + pt.getString(R.string.tx_info_failCnt) + msg.arg1);
                    break;
                case GETSCANRECORD:// 扫描到记录
                    @SuppressWarnings("unchecked")
                    Vector<String> dataList = (Vector<String>) msg.obj;
                    for (String str : dataList) {
                        b_find = false;
                        for (int i = 0; i < pt.scanfReportList.size(); i++) {
                            ScanReport mReport = pt.scanfReportList.get(i);
                            if (str.equals(mReport.getDataStr())) {
                                mReport.setFindCnt(mReport.getFindCnt() + 1);
                                b_find = true;
                            }
                        }
                        if (!b_find) {
                            pt.scanfReportList.add(new ScanReport(str));
                        }

                    }
                    pt.tv_scanRecordInfo.setText(pt
                            .getString(R.string.tx_info_scanfCnt)
                            + pt.scanfReportList.size());
                    pt.scanfAdapter.notifyDataSetChanged();
                    break;
                case THREAD_END:// 线程结束
                    pt.FinishInventory();
                    break;
                default:
                    break;
            }
        }
    }

    private boolean b_inventoryThreadRun = false;

    private class InventoryThrd implements Runnable {
        @Override
        public void run() {
            int failedCnt = 0;// 操作失败次数
            Object hInvenParamSpecList = null;
            byte newAI = RfidDef.AI_TYPE_NEW;
            if (bOnlyReadNew) {
                newAI = RfidDef.AI_TYPE_CONTINUE;
            }
            if (!bUseDefaultPara) {
                hInvenParamSpecList = ADReaderInterface
                        .RDR_CreateInvenParamSpecList();
                ISO15693Interface.ISO15693_CreateInvenParam(
                        hInvenParamSpecList, (byte) 0, bMathAFI, mAFIVal,
                        (byte) 0);
            }
            b_inventoryThreadRun = true;
            while (b_inventoryThreadRun) {
                if (mHandler.hasMessages(INVENTORY_MSG)) {
                    continue;
                }
                int iret = m_reader.RDR_TagInventory(newAI, null, 0,
                        hInvenParamSpecList);
                if (iret == ApiErrDefinition.NO_ERROR
                        || iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                    Vector<ISO15693Tag> tagList = new Vector<ISO15693Tag>();
                    newAI = RfidDef.AI_TYPE_NEW;
                    if (bOnlyReadNew
                            || iret == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                        newAI = RfidDef.AI_TYPE_CONTINUE;
                    }

                    Object tagReport = m_reader
                            .RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
                    while (tagReport != null) {
                        ISO15693Tag tagData = new ISO15693Tag();
                        iret = ISO15693Interface.ISO15693_ParseTagDataReport(
                                tagReport, tagData);
                        if (iret == ApiErrDefinition.NO_ERROR) {
                            tagList.add(tagData);
                        }
                        tagReport = m_reader
                                .RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
                    }
                    if (!tagList.isEmpty()) {
                        if (bBuzzer) {
                            RFIDListActivity.this.playVoice();
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = INVENTORY_MSG;
                        msg.obj = tagList;
                        msg.arg1 = failedCnt;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    newAI = RfidDef.AI_TYPE_NEW;
                    if (b_inventoryThreadRun) {
                        failedCnt++;
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = INVENTORY_FAIL_MSG;
                    msg.arg1 = failedCnt;
                    mHandler.sendMessage(msg);
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }
            }
            b_inventoryThreadRun = false;
            m_reader.RDR_ResetCommuImmeTimeout();
            mHandler.sendEmptyMessage(THREAD_END);// 盘点结束
        }
    }

    ;

    private boolean bGetScanRecordFlg = false;

    private class GetScanRecordThrd implements Runnable {
        @Override
        public void run() {
            int nret = 0;
            bGetScanRecordFlg = true;
            byte gFlg = 0x00;// 初次采集数据或者上一次采集数据失败时，标志位为0x00
            Object dnhReport = null;

            // 清空缓冲区记录
            nret = m_reader.RPAN_ClearScanRecord();
            if (nret != ApiErrDefinition.NO_ERROR) {
                bGetScanRecordFlg = false;
                mHandler.sendEmptyMessage(THREAD_END);// 盘点结束
                return;
            }

            while (bGetScanRecordFlg) {
                if (mHandler.hasMessages(GETSCANRECORD)) {
                    continue;
                }
                nret = m_reader.RPAN_GetRecord(gFlg);
                if (nret != ApiErrDefinition.NO_ERROR) {
                    gFlg = 0x00;
                    continue;
                }
                gFlg = 0x01;// 数据获取成功，将标志位设置为0x01
                dnhReport = m_reader
                        .RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
                Vector<String> dataList = new Vector<String>();
                while (dnhReport != null) {
                    byte[] byData = m_reader.RPAN_ParseRecord(dnhReport);
                    String strData = GFunction.encodeHexStr(byData);
                    dataList.add(strData);
                    dnhReport = m_reader
                            .RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
                }
                if (!dataList.isEmpty()) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = GETSCANRECORD;
                    msg.obj = dataList;
                    mHandler.sendMessage(msg);
                }
            }
            bGetScanRecordFlg = false;
            mHandler.sendEmptyMessage(THREAD_END);// 结束
        }
    }

    ;

    private void saveHistory(String sKey, String val) {
        @SuppressWarnings("deprecation")
        SharedPreferences preferences = this.getSharedPreferences(sKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(sKey, val);
        editor.commit();
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    private void saveHistory(String sKey, int val) {
        SharedPreferences preferences = this.getSharedPreferences(sKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(sKey, val);
        editor.commit();
    }

    private int GetHistoryInt(String sKey) {
        @SuppressWarnings("deprecation")
        @SuppressLint("WorldReadableFiles")
        SharedPreferences preferences = this.getSharedPreferences(sKey,
                Context.MODE_PRIVATE);
        return preferences.getInt(sKey, 0);
    }

    private String GetHistoryString(String sKey) {
        @SuppressWarnings("deprecation")
        @SuppressLint("WorldReadableFiles")
        SharedPreferences preferences = this.getSharedPreferences(sKey,
                Context.MODE_PRIVATE);
        return preferences.getString(sKey, "");
    }

    private void SaveActivity() {
        int devItem = 0;
        int commItem = 0;

        saveHistory("DEVNAME", devItem);
        saveHistory("COMMTYPE", commItem);
    }

}