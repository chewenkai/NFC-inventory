package com.kevin.rfidmanager.Fragments;

/**
 * Created by Kevin on 2017/1/26.
 */

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.SPUtil;

public class SettingFrag extends android.support.v4.app.Fragment {
    Button backupDatabaseButton, restoreDatabaseButton, changePasswordButton, changeRFIDRangeButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_layout, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        backupDatabaseButton = (Button) v.findViewById(R.id.backup_database_button);
        restoreDatabaseButton = (Button) v.findViewById(R.id.restore_database_button);
        changePasswordButton = (Button) v.findViewById(R.id.change_password);
        changeRFIDRangeButton = (Button) v.findViewById(R.id.change_rfid_range);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordChangeDialog();
            }
        });
    }

    /*
    This is a dialog used for changing password.
     */
    public void showPasswordChangeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                // check current password
                if (!SPUtil.getInstence(getActivity().getApplicationContext()).getPassWord().
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
                SPUtil us = SPUtil.getInstence(getActivity().getApplicationContext());
                us.savePassWord(newPasswordEdt.getText().toString());
                Toast.makeText(getActivity().getApplicationContext(), R.string.password_saved, Toast.LENGTH_LONG).
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


}
