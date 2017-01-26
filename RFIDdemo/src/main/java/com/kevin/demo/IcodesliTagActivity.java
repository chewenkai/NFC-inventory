package com.kevin.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.rfid.api.GFunction;
import com.rfid.api.ISO15693Interface;
import com.rfid.api.ISO15693Tag;
import com.rfid.def.ApiErrDefinition;
import com.rfid.def.RfidDef;

import java.util.ArrayList;

public class IcodesliTagActivity extends Activity implements OnClickListener {
    private Button btn_inventory = null;
    private Button btn_icodesli_connect = null;
    private Button btn_icodesli_tagInfo = null;
    private Button btn_icodesli_disconnect = null;
    private Spinner sn_icodesli_uidList = null;
    private Spinner sn_icodesli_connectMode = null;
    private Spinner sn_icodesli_blkAddr = null;
    private Spinner sn_icodesli_blkNum = null;
    private EditText ed_icodesli_blkData = null;
    private Button btn_icodesli_read = null;
    private Button btn_icodesli_write = null;
    private Button btn_icodesli_lockBlk = null;
    private EditText ed_icodesli_dsfid = null;
    private EditText ed_icodesli_afi = null;
    private Button btn_icodesli_setDsfid = null;
    private Button btn_icodesli_lockDsfid = null;
    private Button btn_icodesli_setAFI = null;
    private Button btn_icodesli_lockAFI = null;
    private Button btn_icodesli_enEAS = null;
    private Button btn_icodesli_disEnEAS = null;
    private Button btn_icodesli_checkEAS = null;
    private Button btn_icodesli_lockEas = null;
    private ISO15693Interface mTag = new ISO15693Interface();
    private ArrayList<CharSequence> uidList = new ArrayList<CharSequence>();
    private ArrayAdapter<CharSequence> m_adaUidList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_icodesli);
        btn_inventory = (Button) findViewById(R.id.btn_icodesli_inventory);
        btn_icodesli_connect = (Button) findViewById(R.id.btn_icodesli_connect);
        btn_icodesli_tagInfo = (Button) findViewById(R.id.btn_icodesli_tagInfo);
        btn_icodesli_disconnect = (Button) findViewById(R.id.btn_icodesli_disconnect);
        sn_icodesli_uidList = (Spinner) findViewById(R.id.sn_icodesli_uidList);
        sn_icodesli_connectMode = (Spinner) findViewById(R.id.sn_icodesli_connectMode);
        sn_icodesli_blkAddr = (Spinner) findViewById(R.id.sn_icodesli_blkAddr);
        sn_icodesli_blkNum = (Spinner) findViewById(R.id.sn_icodesli_blkNum);
        ed_icodesli_blkData = (EditText) findViewById(R.id.ed_icodesli_blkData);
        btn_icodesli_read = (Button) findViewById(R.id.btn_icodesli_read);
        btn_icodesli_write = (Button) findViewById(R.id.btn_icodesli_write);
        btn_icodesli_lockBlk = (Button) findViewById(R.id.btn_icodesli_lockBlk);
        ed_icodesli_dsfid = (EditText) findViewById(R.id.ed_icodesli_dsfid);
        ed_icodesli_afi = (EditText) findViewById(R.id.ed_icodesli_afi);
        btn_icodesli_setDsfid = (Button) findViewById(R.id.btn_icodesli_setDsfid);
        btn_icodesli_lockDsfid = (Button) findViewById(R.id.btn_icodesli_lockDsfid);
        btn_icodesli_setAFI = (Button) findViewById(R.id.btn_icodesli_setAFI);
        btn_icodesli_lockAFI = (Button) findViewById(R.id.btn_icodesli_lockAFI);
        btn_icodesli_enEAS = (Button) findViewById(R.id.btn_icodesli_enEAS);
        btn_icodesli_disEnEAS = (Button) findViewById(R.id.btn_icodesli_disEnEAS);
        btn_icodesli_checkEAS = (Button) findViewById(R.id.btn_icodesli_checkEAS);
        btn_icodesli_lockEas = (Button) findViewById(R.id.btn_icodesli_lockEas);

        btn_inventory.setOnClickListener(this);
        btn_icodesli_connect.setOnClickListener(this);
        btn_icodesli_tagInfo.setOnClickListener(this);
        btn_icodesli_disconnect.setOnClickListener(this);
        btn_icodesli_read.setOnClickListener(this);
        btn_icodesli_write.setOnClickListener(this);
        btn_icodesli_lockBlk.setOnClickListener(this);
        btn_icodesli_setDsfid.setOnClickListener(this);
        btn_icodesli_lockDsfid.setOnClickListener(this);
        btn_icodesli_setAFI.setOnClickListener(this);
        btn_icodesli_lockAFI.setOnClickListener(this);
        btn_icodesli_enEAS.setOnClickListener(this);
        btn_icodesli_disEnEAS.setOnClickListener(this);
        btn_icodesli_lockEas.setOnClickListener(this);
        btn_icodesli_checkEAS.setOnClickListener(this);

        ed_icodesli_blkData
                .setOnFocusChangeListener(new OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (ed_icodesli_blkData.hasFocus()) {
                            ed_icodesli_blkData.setError(null, null);
                        }
                    }
                });

        ed_icodesli_dsfid.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View arg0, boolean arg1) {
                if (ed_icodesli_dsfid.hasFocus()) {
                    ed_icodesli_dsfid.setError(null, null);
                }
            }
        });

        ed_icodesli_afi.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View arg0, boolean arg1) {
                if (ed_icodesli_afi.hasFocus()) {
                    ed_icodesli_afi.setError(null, null);
                }
            }
        });

        m_adaUidList = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_dropdown_item, uidList);
        sn_icodesli_uidList.setAdapter(m_adaUidList);

        UiVisible(false);
    }

    private void UiInventory() {
        uidList.clear();
        int iret = -1;
        iret = MainActivity.m_reader.RDR_TagInventory(RfidDef.AI_TYPE_NEW,
                null, 0, null);
        if (iret != ApiErrDefinition.NO_ERROR) {
            return;
        }
        Object tagReport = MainActivity.m_reader
                .RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST);
        while (tagReport != null) {
            ISO15693Tag tagData = new ISO15693Tag();
            iret = ISO15693Interface.ISO15693_ParseTagDataReport(tagReport,
                    tagData);
            if (iret == ApiErrDefinition.NO_ERROR) {
                String uidStr = GFunction.encodeHexStr(tagData.uid);
                uidList.add(uidStr);
            }
            tagReport = MainActivity.m_reader
                    .RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT);
        }
        m_adaUidList.notifyDataSetChanged();
    }

    private void UiVisible(boolean bConnect) {
        btn_inventory.setEnabled(!bConnect);
        btn_icodesli_connect.setEnabled(!bConnect);
        btn_icodesli_tagInfo.setEnabled(bConnect);
        btn_icodesli_disconnect.setEnabled(bConnect);
        sn_icodesli_uidList.setEnabled(!bConnect);
        sn_icodesli_connectMode.setEnabled(!bConnect);
        sn_icodesli_blkAddr.setEnabled(bConnect);
        sn_icodesli_blkNum.setEnabled(bConnect);
        ed_icodesli_blkData.setEnabled(bConnect);
        btn_icodesli_read.setEnabled(bConnect);
        btn_icodesli_write.setEnabled(bConnect);
        btn_icodesli_lockBlk.setEnabled(bConnect);
        ed_icodesli_dsfid.setEnabled(bConnect);
        btn_icodesli_setDsfid.setEnabled(bConnect);
        btn_icodesli_lockDsfid.setEnabled(bConnect);
        btn_icodesli_setAFI.setEnabled(bConnect);
        btn_icodesli_lockAFI.setEnabled(bConnect);
        btn_icodesli_enEAS.setEnabled(bConnect);
        btn_icodesli_disEnEAS.setEnabled(bConnect);
        btn_icodesli_checkEAS.setEnabled(bConnect);
        btn_icodesli_lockEas.setEnabled(bConnect);
        ed_icodesli_afi.setEnabled(bConnect);
    }

    private void UiConnect() {
        byte connectMode = 1;
        if (sn_icodesli_connectMode.getSelectedItemId() == 1) {
            connectMode = 0;
        }
        byte connectUid[] = null;
        if (sn_icodesli_uidList.getSelectedItem() != null) {
            connectUid = GFunction.decodeHex(sn_icodesli_uidList
                    .getSelectedItem().toString());
        }

        if (connectMode == 1 && connectUid == null) {
            new AlertDialog.Builder(this).setTitle("").setMessage("UID不能为空！")
                    .setPositiveButton("确定", null).show();
            return;
        }

        int iret = mTag.ISO15693_Connect(MainActivity.m_reader,
                RfidDef.RFID_ISO15693_PICC_ICODE_SLI_ID, connectMode,
                connectUid);
        if (iret != ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this).setTitle("").setMessage("操作失败！")
                    .setPositiveButton("确定", null).show();
        }
        UiVisible(true);
    }

    @SuppressLint("DefaultLocale")
    private void UiGetTagInfo() {
        byte infoUid[] = new byte[8];
        Byte dsfid = new Byte((byte) 0);
        Byte afi = new Byte((byte) 0);
        Long blkSize = (long) 0;
        Long numOfBloks = (long) 0;
        Byte icRef = new Byte((byte) 0);
        int iret = mTag.ISO15693_GetSystemInfo(infoUid, dsfid, afi, blkSize,
                numOfBloks, icRef);
        if (iret != ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this).setTitle("")
                    .setMessage("获取标签信息失败！err=" + iret)
                    .setPositiveButton("确定", null).show();
        } else {
            String sUid = GFunction.encodeHexStr(infoUid);
            String tagInfo = String
                    .format("Uid:%s\nDSFID:0x%02X\nAFI:0x%02X\nBlkSize:%d\nNumOfBloks:%d\nIcRef:0x%02X",
                            sUid, dsfid, afi, blkSize, numOfBloks, icRef);
            new AlertDialog.Builder(this).setTitle("").setMessage(tagInfo)
                    .setPositiveButton("确定", null).show();
        }
    }

    private void UiReadBlock() {
        int blkAddr = (int) sn_icodesli_blkAddr.getSelectedItemId();
        int numOfBlksToRead = (int) (sn_icodesli_blkNum.getSelectedItemId() + 1);
        if (blkAddr + numOfBlksToRead > 28)// 数据块地址溢出
        {
            numOfBlksToRead = 28 - blkAddr;
        }
        Integer numOfBlksRead = 0;
        Long bytesBlkDatRead = (long) 0;
        byte bufBlocks[] = new byte[4 * numOfBlksToRead];
        int iret = mTag.ISO15693_ReadMultiBlocks(false, blkAddr,
                numOfBlksToRead, numOfBlksRead, bufBlocks, bytesBlkDatRead);
        if (iret != ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this).setTitle("")
                    .setMessage("读数据块失败！err" + iret)
                    .setPositiveButton("确定", null).show();
        }
        String strData = GFunction.encodeHexStr(bufBlocks);
        ed_icodesli_blkData.setText(strData);
    }

    private void UiWriteBlock() {
        String strData = ed_icodesli_blkData.getText().toString();
        byte byData[] = GFunction.decodeHex(strData);
        if (byData == null) {
            ed_icodesli_blkData.setError("请输入一个十六进制字符串!");
            return;
        }
        int blkNum = sn_icodesli_blkNum.getSelectedItemPosition() + 1;
        int blkAddr = sn_icodesli_blkAddr.getSelectedItemPosition();
        if (blkNum * 4 != byData.length) {
            ed_icodesli_blkData.setError("数据长度不正确");
            return;
        }
        if (blkAddr + blkNum > 28) {
            new AlertDialog.Builder(this).setMessage("参数不正确")
                    .setPositiveButton("确定", null).show();
            return;
        }
        int iret = mTag.ISO15693_WriteMultipleBlocks(blkAddr, blkNum, byData);
        String srtResult = "";
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "写数据块成功！";

        } else {
            srtResult = "写数据块失败！err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiLockBlock() {
        int blkAddr = sn_icodesli_blkAddr.getSelectedItemPosition();
        int blkCnt = sn_icodesli_blkNum.getSelectedItemPosition() + 1;
        int iret = mTag.ISO15693_LockMultipleBlocks(blkAddr, blkCnt);
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "锁数据块成功!";
        } else {
            srtResult = "锁数据块失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiWriteDsfid() {
        String dsfidStr = ed_icodesli_dsfid.getText().toString();
        byte dsfid[] = GFunction.decodeHex(dsfidStr);// Byte.parseByte(dsfidStr,
        // 16);
        if (dsfid == null) {
            ed_icodesli_dsfid.setError("请输入DSFID值");
            return;
        }
        int iret = mTag.ISO15693_WriteDSFID(dsfid[0]);
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "设置DSFID成功!";
        } else {
            srtResult = "设置DSFID失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiWriteAFI() {
        String afiStr = ed_icodesli_afi.getText().toString();
        byte afi[] = GFunction.decodeHex(afiStr);
        if (afi == null) {
            ed_icodesli_afi.setError("请输入AFI值");
            return;
        }
        int iret = mTag.ISO15693_WriteAFI(afi[0]);
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "设置AFI成功!";
        } else {
            srtResult = "设置AFI失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiLockDsfid() {
        int iret = mTag.ISO15693_LockDSFID();
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "锁DSFID成功!";
        } else {
            srtResult = "锁DSFID失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiEnableEAS() {
        int iret = mTag.NXPICODESLI_EableEAS();
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "使能EAS成功!";
        } else {
            srtResult = "使能EAS失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiDisableEAS() {
        int iret = mTag.NXPICODESLI_DisableEAS();
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "禁止EAS成功!";
        } else {
            srtResult = "禁止EAS失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiLockEAS() {
        int iret = mTag.NXPICODESLI_LockEAS();
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "锁EAS成功!";
        } else {
            srtResult = "锁EAS失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiLockAFI() {
        int iret = mTag.ISO15693_LockAFI();
        String srtResult;
        if (iret == ApiErrDefinition.NO_ERROR) {
            srtResult = "锁AFI成功!";
        } else {
            srtResult = "锁AFI失败!err=" + iret;
        }
        new AlertDialog.Builder(this).setTitle("").setMessage(srtResult)
                .setPositiveButton("确定", null).show();
    }

    private void UiEasCheck() {
        Byte easFlg = new Byte((byte) 0);
        int iret = mTag.NXPICODESLI_EASCheck(easFlg);
        if (iret != ApiErrDefinition.NO_ERROR) {
            new AlertDialog.Builder(this).setTitle("")
                    .setMessage("检测失败!err=" + iret)
                    .setPositiveButton("确定", null).show();
            return;
        }
        if (easFlg.byteValue() == 0) {
            new AlertDialog.Builder(this).setTitle("").setMessage("EAS已经关闭")
                    .setPositiveButton("确定", null).show();
        } else {
            new AlertDialog.Builder(this).setTitle("").setMessage("EAS已经打开")
                    .setPositiveButton("确定", null).show();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_icodesli_inventory:
                UiInventory();
                break;
            case R.id.btn_icodesli_connect:
                UiConnect();
                break;
            case R.id.btn_icodesli_tagInfo:
                UiGetTagInfo();
                break;
            case R.id.btn_icodesli_disconnect:
                mTag.ISO15693_Disconnect();
                UiVisible(false);
                break;
            case R.id.btn_icodesli_read:
                UiReadBlock();
                break;
            case R.id.btn_icodesli_write:
                UiWriteBlock();
                break;
            case R.id.btn_icodesli_lockBlk:
                UiLockBlock();
                break;
            case R.id.btn_icodesli_setDsfid:
                UiWriteDsfid();
                break;
            case R.id.btn_icodesli_lockDsfid:
                UiLockDsfid();
                break;
            case R.id.btn_icodesli_setAFI:
                UiWriteAFI();
                break;
            case R.id.btn_icodesli_lockAFI:
                UiLockAFI();
                break;
            case R.id.btn_icodesli_enEAS:
                UiEnableEAS();
                break;
            case R.id.btn_icodesli_disEnEAS:
                UiDisableEAS();
                break;
            case R.id.btn_icodesli_lockEas:
                UiLockEAS();
                break;
            case R.id.btn_icodesli_checkEAS:
                UiEasCheck();
                break;
            default:
                break;
        }
    }
}
