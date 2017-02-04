package com.kevin.rfidmanager.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.SPUtil;
import com.kevin.rfidmanager.Utils.StringUtil;

import static com.kevin.rfidmanager.Utils.ConstantManager.IS_DEBUGING;
import static com.kevin.rfidmanager.Utils.ConstantManager.PERMISSION_REQUEST_CODE;


/**
 * Login UI
 * Created by Kevin on 2017/1/26
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mPersonEdit;   // user name editor
    private EditText mPwdEdit;  // Password text editor
    private Button mLoginBtn, mRegisterButton;   // Login button
    private String mUserNameStr, mPwdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIsNeedPassword();  // If do not need password, go to main page directly.
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();   // Hide ActionBar
        checkPermission();
        findView();
        initView();

    }

    /*
     * find view in layout file.(xml file)
     */
    private void findView() {
        mPersonEdit = (EditText) findViewById(R.id.login_activity_personname_edittext);
        mPwdEdit = (EditText) findViewById(R.id.login_activity_password_edittext);
        mLoginBtn = (Button) findViewById(R.id.login_activity_login_btn);
        mRegisterButton = (Button) findViewById(R.id.login_activity_regist_btn);
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
                mUserNameStr = mPersonEdit.getText().toString();
                if (StringUtil.isEmpty(mUserNameStr)) {
                    Snackbar.make(v, R.string.empty_username_warning, Snackbar.LENGTH_LONG).show();
                    return;
                }
                mPwdStr = mPwdEdit.getText().toString();
                if (StringUtil.isEmpty(mPwdStr)) {
                    Snackbar.make(v, R.string.empty_password_warning, Snackbar.LENGTH_LONG).show();
                    return;
                }

                // compare two string
                SPUtil us = SPUtil.getInstence(getApplicationContext());
                String rightPassword = us.getPassWord();
                String rightUsername = us.getPersonName();
                if (mPwdStr.equals(rightPassword) && mUserNameStr.equals(rightUsername)){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Snackbar.make(v, R.string.login_fail, Snackbar.LENGTH_LONG).show();
                }
                packUpImm();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordInputDialog();
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
        if (rightPassword.isEmpty() && SPUtil.getInstence(getApplicationContext()).getNeedPassword()) {
            // first time using this app
            showPasswordInputDialog();
        }
    }

    /*
    This is a dialog used for input new password when user have not set up any password.
     */
    public void showPasswordInputDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_input_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText usernameEdt = (EditText) dialogView.findViewById(R.id.username_editor);
        final EditText firstPasswordEdt = (EditText) dialogView.findViewById(R.id.password_editor);
        final EditText confirmPasswordEdt = (EditText) dialogView.findViewById(R.id.confirm_password);
        final TextView message = (TextView) dialogView.findViewById(R.id.message_text_login);
        final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.skip_pswd_checkbox);

        final TextView saveButton = (TextView) dialogView.findViewById(R.id.dialog_ok);
        final TextView cancleButton = (TextView) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(R.string.welcome);
        dialogBuilder.setMessage(R.string.password_input_reminder);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check CheckBox status
                if (checkBox.isChecked()) {
                    SPUtil.getInstence(getApplicationContext()).saveNeedPassword(false);
                    Toast.makeText(getApplicationContext(), R.string.password_omitted, Toast.LENGTH_LONG).
                            show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    b.dismiss();
                    finish();
                    return;
                }
                if (usernameEdt.getText().toString().isEmpty()) {
                    message.setText(R.string.empty_username_warning);
                    message.setTextColor(getResources().getColor(R.color.warning_color));
                    return;
                }
                if (firstPasswordEdt.getText().toString().isEmpty() ||
                        confirmPasswordEdt.getText().toString().isEmpty()) {
                    message.setText(R.string.empty_password_warning);
                    message.setTextColor(getResources().getColor(R.color.warning_color));
                    return;
                }
                // check password of two text editors
                if (!firstPasswordEdt.getText().toString().
                        equals(confirmPasswordEdt.getText().toString())) {
                    message.setText(R.string.diff_passwd);
                    message.setTextColor(getResources().getColor(R.color.warning_color));
                    return;
                }
                //save password with edt.getText().toString();
                SPUtil us = SPUtil.getInstence(getApplicationContext());
                us.savePassWord(firstPasswordEdt.getText().toString());
                us.savePersonName(usernameEdt.getText().toString());
                Toast.makeText(getApplicationContext(), R.string.password_saved, Toast.LENGTH_LONG).
                        show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                b.dismiss();
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit system
               finish();
            }
        });
    }

    /*
    Check that if user need password to protect their information.
     */
    private void checkIsNeedPassword() {
        if (!SPUtil.getInstence(getApplicationContext()).getNeedPassword()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
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
