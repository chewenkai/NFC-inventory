package com.kevin.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class InventoryParaActivity extends Activity implements OnClickListener {
    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCEL = 2;
    private CheckBox check_useDefault = null;
    private CheckBox check_onlyReadNew = null;
    private CheckBox check_mathAFI = null;
    private CheckBox check_buzzer = null;
    private EditText ed_afiVal = null;
    private Button btn_ok = null;
    private Button btn_cancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_set);

        check_useDefault = (CheckBox) findViewById(R.id.check_useDefault);
        check_mathAFI = (CheckBox) findViewById(R.id.check_matchAFI);
        check_onlyReadNew = (CheckBox) findViewById(R.id.check_onlyReadNew);
        check_buzzer = (CheckBox) findViewById(R.id.check_buzzer);
        ed_afiVal = (EditText) findViewById(R.id.ed_afiVal);
        btn_ok = (Button) findViewById(R.id.btn_inventorySetOk);
        btn_cancel = (Button) findViewById(R.id.btn_inventorySetCancel);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        check_useDefault
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean isCheck) {
                        if (isCheck) {
                            check_mathAFI.setEnabled(false);
                            ed_afiVal.setEnabled(false);
                        } else {
                            check_mathAFI.setEnabled(true);
                            ed_afiVal.setEnabled(true);
                        }
                    }

                });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boolean bUseDefaultPara = bundle.getBoolean("UseDefaultPara");
        boolean bOnlyReadNew = bundle.getBoolean("OnlyReadNew");
        boolean bMathAFI = bundle.getBoolean("MathAFI");
        boolean bBuzzer = bundle.getBoolean("bBuzzer");
        byte mAFIVal = bundle.getByte("AFI");

        check_useDefault.setChecked(bUseDefaultPara);
        check_onlyReadNew.setChecked(bOnlyReadNew);
        check_mathAFI.setChecked(bMathAFI);
        check_buzzer.setChecked(bBuzzer);
        ed_afiVal.setText(String.format("%02X", mAFIVal));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_inventorySetOk:
                int afiTmp = 0;
                boolean bErr = false;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                try {
                    afiTmp = Integer.parseInt(ed_afiVal.getText().toString(), 16);
                    if (afiTmp < 0 || afiTmp >= 256) {
                        bErr = true;
                    }
                } catch (Exception e) {
                    bErr = true;

                }
                if (bErr) {
                    new AlertDialog.Builder(this).setTitle("")
                            .setMessage("Please input a AFI hex value 请输入一个16进制的AFI值!")
                            .setPositiveButton("OK 确定", null).show();
                    break;
                }
                bundle.putBoolean("UseDefaultPara", check_useDefault.isChecked());
                bundle.putBoolean("OnlyReadNew", check_onlyReadNew.isChecked());
                bundle.putBoolean("MathAFI", check_mathAFI.isChecked());
                bundle.putByte("AFI", (byte) (afiTmp & 0xff));
                bundle.putBoolean("bBuzzer", check_buzzer.isChecked());
                intent.putExtras(bundle);
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            default:
                this.finish();
                break;
        }
    }
}
