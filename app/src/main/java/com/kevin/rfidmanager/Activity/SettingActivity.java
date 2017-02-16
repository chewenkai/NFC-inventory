package com.kevin.rfidmanager.Activity;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.SPUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.Users;
import com.kevin.rfidmanager.database.UsersDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import at.markushi.ui.CircleButton;

public class SettingActivity extends AppCompatActivity {
    Button changeApperance, backupDatabaseButton, restoreDatabaseButton, changePasswordButton, changeRFIDRangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initUI();
    }
    private void initUI() {
        changeApperance = (Button) findViewById(R.id.change_theme);
        changeApperance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeApperanceDialog();
            }
        });
        backupDatabaseButton = (Button) findViewById(R.id.backup_database_button);
        backupDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupDialog();
            }
        });
        restoreDatabaseButton = (Button) findViewById(R.id.restore_database_button);
        restoreDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreDialog();
            }
        });
        changePasswordButton = (Button) findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordChangeDialog();
            }
        });

        changeRFIDRangeButton = (Button) findViewById(R.id.change_rfid_range);

    }

    private void changeApperanceDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_change_apperance_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.select_an_appearance);
        dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog b = dialogBuilder.create();
        final TextView textView = (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final CircleButton linear_layout = (CircleButton) dialogView.findViewById(R.id.linear_layout);
        final CircleButton staggered_layout = (CircleButton) dialogView.findViewById(R.id.staggered_layout);
        final CircleButton one_row_layout = (CircleButton) dialogView.findViewById(R.id.one_row_layout);

        switch (SPUtil.getInstence(SettingActivity.this).getApperance()){
            case 8:  // ConstantManager.LINEAR_LAYOUT
                textView.setText("Current selection: Linear Layout");
                break;
            case 9:  // ConstantManager.STAGGER_LAYOUT
                textView.setText("Current selection: Staggered Layout");
                break;
            case 10:  // ConstantManager.ONE_ROW_LAYOUT
                textView.setText("Current selection: One Row Layout");
                break;
        }

        linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(SettingActivity.this).setApperance(ConstantManager.LINEAR_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                b.dismiss();

            }
        });

        staggered_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(SettingActivity.this).setApperance(ConstantManager.STAGGER_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                b.dismiss();
            }
        });

        one_row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(SettingActivity.this).setApperance(ConstantManager.ONE_ROW_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                b.dismiss();
            }
        });
        b.show();
    }

    /*
    This is a dialog used for changing password.
     */
    public void showPasswordChangeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_change_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText oldPasswordEdt = (EditText) dialogView.findViewById(R.id.old_password_editor);
        final EditText newPasswordEdt = (EditText) dialogView.findViewById(R.id.new_password_editor);
        final EditText confirmNewPasswordEdt = (EditText) dialogView.findViewById(R.id.confirm_new_password);
        final TextView message = (TextView) dialogView.findViewById(R.id.message_text_login);
        final Button saveButton = (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton = (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(getResources().getString(R.string.change_passwd));
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                UsersDao usersDao = daoSession.getUsersDao();


                List<Users> users = DatabaseUtil.queryUsers(SettingActivity.this, ((MyApplication) getApplication()).getUserName());
                if (users.size() > 1) {
                    ((MyApplication) getApplication()).toast(getString(R.string.illegal_user));
                    usersDao.deleteInTx(users);
                    return;
                } else {
                    Users user = users.get(0);
                    // check current password
                    if (!user.getPassWord().
                            equals(oldPasswordEdt.getText().toString())) {
                        message.setText(R.string.wrong_old_password);
                        message.setTextColor(getResources().getColor(R.color.warning_color));
                        return;
                    }
                    // check password of two text editors
                    if (!newPasswordEdt.getText().toString().
                            equals(confirmNewPasswordEdt.getText().toString())) {
                        message.setText(R.string.diff_passwd);
                        message.setTextColor(getResources().getColor(R.color.warning_color));
                        return;
                    }
                    //save password with edt.getText().toString();

                    user.setPassWord(newPasswordEdt.getText().toString());
                    usersDao.insertOrReplace(user);
                }

                Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_LONG).
                        show();
                b.dismiss();
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog
                b.dismiss();
            }
        });
    }


    /*
    This is a dialog used for backup database
     */
    public void backupDialog() {

    }

    /*
    This is a dialog used for backup database
     */
    public void restoreDialog() {

    }
}
