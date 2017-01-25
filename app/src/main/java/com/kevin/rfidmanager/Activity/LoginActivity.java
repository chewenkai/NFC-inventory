package com.kevin.rfidmanager.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ExitApplication;
import com.kevin.rfidmanager.Utils.SPUtil;
import com.kevin.rfidmanager.Utils.StringUtil;
import com.kevin.rfidmanager.Utils.SysUtil;

import static com.kevin.rfidmanager.Utils.ConstantManager.IS_DEBUGING;


/**
 * Login UI
 * Created by Kevin on 2017/1/26
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mPwdEdit;  // Password text editor
    private Button mLoginBtn;   // Login button
    private String mPwdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ExitApplication.getInstance().addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();   // Hide ActionBar
        findView();
        initView();
    }

    /*
     * find view in layout file.(xml file)
     */
    private void findView() {
        mPwdEdit = (EditText) findViewById(R.id.login_activity_password_edittext);
        mLoginBtn = (Button) findViewById(R.id.login_activity_login_btn);
    }

    /*
     * init UI
     */
    private void initView() {

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The button is clicked.
                if (IS_DEBUGING) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                mPwdStr = mPwdEdit.getText().toString();
                if (StringUtil.isEmpty(mPwdStr))
                    Snackbar.make(v, R.string.empty_warning, Snackbar.LENGTH_LONG).show();

                // compare two string
                SPUtil us = SPUtil.getInstence(getApplicationContext());
                String rightPassword = us.getPassWord();
                if (mPwdStr.equals(rightPassword)){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Snackbar.make(v, R.string.login_fail, Snackbar.LENGTH_LONG).show();
                }
                packUpImm();
            }
        });
        passwordReminder();
    }

    /*
    Remind user input password when first opening.
     */
    private void passwordReminder() {
        // Are there any user info?
        SPUtil us = SPUtil.getInstence(getApplicationContext());
        String rightPassword = us.getPassWord();
        if (rightPassword.isEmpty()){
            // first time using this app
            showPasswordInputDialog();
        }
    }

    public void showPasswordInputDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_input_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.password_editor);

        dialogBuilder.setTitle(R.string.welcome);
        dialogBuilder.setMessage(R.string.password_input_reminder);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //save password with edt.getText().toString();
                SPUtil us = SPUtil.getInstence(getApplicationContext());
                us.savePassWord(edt.getText().toString());
                Toast.makeText(getApplicationContext(), R.string.password_saved, Toast.LENGTH_LONG).
                        show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Exit system
                ExitApplication.getInstance().exit();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    /*
    Hide input method
     */
    private void packUpImm() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
