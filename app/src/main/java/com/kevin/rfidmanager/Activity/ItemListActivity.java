package com.kevin.rfidmanager.Activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.kevin.rfidmanager.Adapter.ItemListAdaper;
import com.kevin.rfidmanager.Adapter.StorageDevicesAdaper;
import com.kevin.rfidmanager.Entity.DeviceFile;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ExternalStorage;
import com.kevin.rfidmanager.Utils.SPUtil;
import com.kevin.rfidmanager.Utils.ScreenUtil;
import com.kevin.rfidmanager.database.DaoSession;
import com.kevin.rfidmanager.database.Items;
import com.kevin.rfidmanager.database.ItemsDao;
import com.kevin.rfidmanager.database.Users;
import com.kevin.rfidmanager.database.UsersDao;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

/**
 * Main page of the app
 */
public class ItemListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemListAdaper itemListAdapter;
    final String ACTION_USB_PERMISSION =
            "com.kevin.rfidmanager.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_layout);
        initActionBar();
        initUI();
    }

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.custom_action_bar, null);
        TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.app_name);
        mTitleTextView.setTextColor(getResources().getColor(R.color.black));
        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0, 0);

        int paddingPixels = ScreenUtil.dpToPx(this, 5);
        BoomMenuButton leftBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_left_bmb);

        leftBmb.setButtonEnum(ButtonEnum.Ham);
        leftBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_6);
        leftBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_6);

        HamButton.Builder changeAppearance = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        changeApperanceDialog();
                    }
                })
                .normalImageRes(R.drawable.ic_color_lens_white_48dp)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_apperance)
                .containsSubText(false);
        leftBmb.addBuilder(changeAppearance);

        HamButton.Builder backup = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        backupDialog();
                    }
                })
                .normalImageRes(R.drawable.ic_settings_backup_restore_white_48dp)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.backup_database)
                .containsSubText(false);
        leftBmb.addBuilder(backup);

        HamButton.Builder restore = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        restoreDialog();
                    }
                })
                .normalImageRes(R.drawable.ic_restore_white_48dp)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.restore_database)
                .containsSubText(false);
        leftBmb.addBuilder(restore);

        HamButton.Builder changePassword = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        showPasswordChangeDialog();
                    }
                })
                .normalImageRes(R.drawable.key)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_password)
                .containsSubText(false);
        leftBmb.addBuilder(changePassword);

        HamButton.Builder change_rfid_range = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        //showPasswordChangeDialog();
                    }
                })
                .normalImageRes(R.drawable.range)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.change_rfid_range);
        leftBmb.addBuilder(change_rfid_range);


        HamButton.Builder log_out = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        SPUtil.getInstence(getApplicationContext()).saveNeedPassword(true);
                        startActivity(new Intent(ItemListActivity.this, LoginActivity.class));
                        ((MyApplication) getApplication()).
                                setCurrentItemID(ConstantManager.DEFAULT_RFID);
                        finish();
                    }
                })
                .normalImageRes(R.drawable.logout)
                .imagePadding(new Rect(paddingPixels, paddingPixels, paddingPixels, paddingPixels))
                .normalTextRes(R.string.log_out);
        leftBmb.addBuilder(log_out);

//        rightBmb.setButtonEnum(ButtonEnum.Ham);
//        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
//        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
//        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
//            rightBmb.addBuilder(BuilderManager.getHamButtonBuilder());
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_item_list);
        List<Items> items = DatabaseUtil.queryItems(ItemListActivity.this);

        if (((MyApplication) getApplication()).getCurrentItemID() == ConstantManager.DEFAULT_RFID &&
                items.size() != 0)
            ((MyApplication) getApplication()).setCurrentItemID(items.get(0).getRfid());

        itemListAdapter = new ItemListAdaper(ItemListActivity.this, items);
        recyclerView.setAdapter(itemListAdapter);
        setRecyclerViewLayout();
        recyclerView.setHasFixedSize(true);
        registUSBBroadCast();
    }

    private void registUSBBroadCast() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(usbReceiver);
        super.onDestroy();
    }

    private void setRecyclerViewLayout() {
        switch (SPUtil.getInstence(ItemListActivity.this).getApperance()) {
            case 8:  // ConstantManager.LINEAR_LAYOUT
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ItemListActivity.this,
                        3, GridLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to
                                                                // the recycler view
                break;
            case 9:  // ConstantManager.STAGGER_LAYOUT
                // First param is number of columns and second param is orientation i.e
                // Vertical or Horizontal
                StaggeredGridLayoutManager staggeredGridLayoutManager =
                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
            case 10:  // ConstantManager.ONE_ROW_LAYOUT
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        ItemListActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                break;
        }
    }

    /*
           This is a dialog used for add new key description
            */
    public void addNewItem() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_two_edit_text, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText itemID = (TextInputEditText) dialogView.
                findViewById(R.id.edit_key_des_text_editor);
        final TextInputEditText itemName = (TextInputEditText) dialogView.
                findViewById(R.id.item_name_edit);
        final Button saveButton = (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton = (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle("Just input a number as a ID of RFID card and a name of item");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long new_id = null;
                try {
                    new_id = Long.parseLong(itemID.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(ItemListActivity.this,
                            "please input number as ID", Toast.LENGTH_LONG).show();
                    return;
                }
                // Are there any user info?
                DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                ItemsDao itemsDao = daoSession.getItemsDao();
                List<Items> items = itemsDao.queryBuilder().
                        where(ItemsDao.Properties.Rfid.eq(new_id)).build().list();
                if (items.size() > 0) {
                    Toast.makeText(ItemListActivity.this,
                            "The ID card is exist, please change a ID", Toast.LENGTH_LONG).show();
                    return;
                }
                ((MyApplication) getApplication()).
                        setCurrentItemID(Long.parseLong(itemID.getText().toString()));
                DatabaseUtil.insertNewItem(ItemListActivity.this,
                        Long.parseLong(itemID.getText().toString()),
                        itemName.getText().toString());
                Intent intent = new Intent(ItemListActivity.this, ItemEditActivity.class);
                startActivity(intent);
                b.dismiss();

            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    private void changeApperanceDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemListActivity.this);
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
        final TextView textView =
                (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final CircleButton linear_layout =
                (CircleButton) dialogView.findViewById(R.id.linear_layout);
        final CircleButton staggered_layout =
                (CircleButton) dialogView.findViewById(R.id.staggered_layout);
        final CircleButton one_row_layout =
                (CircleButton) dialogView.findViewById(R.id.one_row_layout);

        switch (SPUtil.getInstence(ItemListActivity.this).getApperance()) {
            case 8:  // ConstantManager.LINEAR_LAYOUT
                textView.setText(R.string.current_selection_line);
                break;
            case 9:  // ConstantManager.STAGGER_LAYOUT
                textView.setText(R.string.current_selection_staggered);
                break;
            case 10:  // ConstantManager.ONE_ROW_LAYOUT
                textView.setText(R.string.current_selection_one_row);
                break;
        }

        linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(ItemListActivity.this).
                        setApperance(ConstantManager.LINEAR_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                initUI();
                b.dismiss();

            }
        });

        staggered_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(ItemListActivity.this).
                        setApperance(ConstantManager.STAGGER_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                initUI();
                b.dismiss();
            }
        });

        one_row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(ItemListActivity.this).
                        setApperance(ConstantManager.ONE_ROW_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                initUI();
                b.dismiss();
            }
        });
        b.show();
    }

    /*
    This is a dialog used for changing password.
     */
    public void showPasswordChangeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_change_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText oldPasswordEdt =
                (EditText) dialogView.findViewById(R.id.old_password_editor);
        final EditText newPasswordEdt =
                (EditText) dialogView.findViewById(R.id.new_password_editor);
        final EditText confirmNewPasswordEdt =
                (EditText) dialogView.findViewById(R.id.confirm_new_password);
        final TextView message =
                (TextView) dialogView.findViewById(R.id.message_text_login);
        final Button saveButton =
                (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton =
                (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle(getResources().getString(R.string.change_passwd));
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                UsersDao usersDao = daoSession.getUsersDao();


                List<Users> users = DatabaseUtil.queryUsers(ItemListActivity.this,
                        ((MyApplication) getApplication()).getUserName());
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

                Toast.makeText(getApplicationContext(),
                        R.string.password_updated, Toast.LENGTH_LONG).
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

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_backup_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.select_backup_position);
        dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog b = null;

        final TextView textView =
                (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final RecyclerView recyclerView =
                (RecyclerView) dialogView.findViewById(R.id.recycle_view_storage_devices_list);
        List<DeviceFile> deviceFiles = getDevicePathSet(textView);
        if (deviceFiles == null) {
            Toast.makeText(ItemListActivity.this,
                    "Do not have access to read USB device, please grant permission.", Toast.LENGTH_LONG).show();
            return;
        }
        final StorageDevicesAdaper storageDevicesAdaper =
                new StorageDevicesAdaper(ItemListActivity.this, deviceFiles);
        recyclerView.setAdapter(storageDevicesAdaper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ItemListActivity.this,
                LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
        recyclerView.setHasFixedSize(true);
        dialogBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (storageDevicesAdaper.selectedDeviceRootPath != null) {
                    if (storageDevicesAdaper.selectedDeviceRootPath.type == ConstantManager.DEFAULT_FILE) {
                        if (copyDBtoStorage(storageDevicesAdaper.selectedDeviceRootPath.defaultFile)) {
                            ((MyApplication) getApplication()).toast(getString(R.string.backup_successful) +
                                    " " + storageDevicesAdaper.selectedDeviceRootPath.deviceName);
                        } else {
                            ((MyApplication) getApplication()).toast(getString(R.string.backup_failed));
                        }
                    } else {
                        if (copyDBtoStorage(storageDevicesAdaper.selectedDeviceRootPath.usbFile)) {
                            ((MyApplication) getApplication()).toast(getString(R.string.backup_successful) +
                                    " " + storageDevicesAdaper.selectedDeviceRootPath.deviceName);
                        } else {
                            ((MyApplication) getApplication()).toast(getString(R.string.backup_failed));
                        }
                    }

                } else {
                    ((MyApplication) getApplication()).
                            toast(getString(R.string.select_at_least_one_item));
                }
            }
        });
        b = dialogBuilder.create();
        b.show();

    }

    /*
    This is a dialog used for backup database
     */
    public void restoreDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ItemListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_backup_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.select_restore_position);
        dialogBuilder.setMessage(R.string.restore_warning);
        dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog b;
        final TextView textView =
                (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final RecyclerView recyclerView =
                (RecyclerView) dialogView.findViewById(R.id.recycle_view_storage_devices_list);
        List<DeviceFile> deviceFiles = getDevicePathSet(textView);
        if (deviceFiles == null) {
            Toast.makeText(ItemListActivity.this,
                    R.string.grant_permission_warning, Toast.LENGTH_LONG).show();
            return;
        }
        final StorageDevicesAdaper storageDevicesAdaper =
                new StorageDevicesAdaper(ItemListActivity.this, deviceFiles);
        recyclerView.setAdapter(storageDevicesAdaper);
        recyclerView.setAdapter(storageDevicesAdaper);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(ItemListActivity.this, LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);// Attach layout manager to the RecyclerView
        recyclerView.setHasFixedSize(true);
        dialogBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (storageDevicesAdaper.selectedDeviceRootPath != null) {
                    if (storageDevicesAdaper.selectedDeviceRootPath.type == ConstantManager.DEFAULT_FILE) {
                        if (copyDBtoAPP(storageDevicesAdaper.selectedDeviceRootPath.defaultFile, textView)) {
                            ((MyApplication) getApplication()).setCurrentItemID(ConstantManager.DEFAULT_RFID);
                            ((MyApplication) getApplication()).toast(getString(R.string.restore_successful));
                            initUI();
                        } else {
                            ((MyApplication) getApplication()).toast(getString(R.string.restore_failed));
                        }
                    } else {
                        if (copyDBtoAPP(storageDevicesAdaper.selectedDeviceRootPath.usbFile, textView,
                                storageDevicesAdaper.selectedDeviceRootPath.device)) {
                            ((MyApplication) getApplication()).setCurrentItemID(ConstantManager.DEFAULT_RFID);
                            ((MyApplication) getApplication()).toast(getString(R.string.restore_successful));
                            initUI();
                        } else {
                            ((MyApplication) getApplication()).toast(getString(R.string.restore_failed));
                        }
                    }

                } else {
                    ((MyApplication) getApplication()).
                            toast(getString(R.string.select_at_least_one_item));
                }
            }
        });
        b = dialogBuilder.create();
        b.show();

    }

    private List<DeviceFile> getDevicePathSet(TextView textView) {
        // Detect primary storage and secondary storage
        List<DeviceFile> pathList = new ArrayList<>();
        Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
        File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
        File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
        if (sdCard != null) {
            DeviceFile deviceFile = new DeviceFile();
            deviceFile.defaultFile = sdCard.getPath();
            deviceFile.type = ConstantManager.DEFAULT_FILE;
            deviceFile.deviceName = getString(R.string.internal_sd_card);
            pathList.add(deviceFile);
        } else if (externalSdCard != null) {
            DeviceFile deviceFile = new DeviceFile();
            deviceFile.defaultFile = externalSdCard.getPath();
            deviceFile.type = ConstantManager.DEFAULT_FILE;
            deviceFile.deviceName = getString(R.string.external_sd_card);
            pathList.add(deviceFile);
        }

        // Detect USB devices
        int counter = 1;
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.
                getMassStorageDevices(ItemListActivity.this);
        for (UsbMassStorageDevice device : devices) {

            // before interacting with a device you need to call init()!
            try {
                PendingIntent permissionIntent = PendingIntent.
                        getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device.getUsbDevice(), permissionIntent);
                device.init();
                // Only uses the first partition on the device
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                DeviceFile deviceFile = new DeviceFile();
                deviceFile.usbFile = currentFs.getRootDirectory();
                deviceFile.type = ConstantManager.USB_FILE;
                deviceFile.device = device;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    deviceFile.deviceName = "USB:" + device.getUsbDevice().getProductName();
                else
                    deviceFile.deviceName = "USB:" + counter++;
                pathList.add(deviceFile);
            } catch (Exception e) {
                e.printStackTrace();
                textView.setText(e.getMessage());
                return null;
            }
        }
        return pathList;
    }

    public boolean copyDBtoStorage(UsbFile targetRoot) {
        try {
            File currentDB = getDatabasePath(getString(R.string.database_name));

            String backupDBName = String.format("%s.bak", getString(R.string.database_name));
            boolean exist = false;
            UsbFile srcFile = null;
            for (UsbFile file :
                    targetRoot.listFiles()) {
                if (file.getName().equals(backupDBName)) {
                    srcFile = file;
                    exist = true;
                }
            }
            if (exist)
                srcFile.delete();

            // write to a file
            OutputStream os = new UsbFileOutputStream(targetRoot.createFile(backupDBName));
            os.write(getByteFromFile(currentDB));
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean copyDBtoAPP(UsbFile srcRoot, TextView textView, UsbMassStorageDevice device) {
        try {
            String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
            File backupDB = getDatabasePath(getString(R.string.database_name));
            boolean exist = false;
            UsbFile srcFile = null;
            for (UsbFile file :
                    srcRoot.listFiles()) {
                if (file.getName().equals(backupDBPath)) {
                    srcFile = file;
                    exist = true;
                }
            }

            if (!exist) {
                textView.setText(R.string.no_backup_File_TF);
                return false;
            }
            CopyTaskParam param = new CopyTaskParam();
            param.from = srcFile;
            param.to = backupDB;
//            new CopyTask(device.getPartitions().get(0).getFileSystem()).execute(param);
//

            OutputStream out = new BufferedOutputStream(new FileOutputStream(param.to));
            InputStream inputStream =
                    UsbFileStreamFactory.createBufferedInputStream(
                            param.from, device.getPartitions().get(0).getFileSystem());
            byte[] bytes = new byte[4096];
            int count;
            int total = 0;

            while ((count = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, count);
                total += count;
            }

            out.close();
            inputStream.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText(e.getMessage());
            return false;
        }
    }

    public boolean copyDBtoStorage(String targetpath) {
        try {
            File currentDB = getDatabasePath(getString(R.string.database_name));

            String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
            File backupDB = new File(targetpath, backupDBPath);
            backupDB.createNewFile();
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean copyDBtoAPP(String srcPath, TextView textView) {
        try {
            String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
            File backupDB = getDatabasePath(getString(R.string.database_name));
            File currentDB = new File(srcPath, backupDBPath);

            if (!backupDB.exists()) {
                textView.setText(R.string.no_backup_File_TF);
                return false;
            }

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
        builder.setMessage(R.string.exit_warning);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itemlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_add:
                addNewItem();
                break;
        }
        return true;
    }

    private byte[] getByteFromFile(File file) throws IOException {
        //init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        return bytesArray;
    }

    /**
     * Class to hold the files for a copy task. Holds the source and the
     * destination file.
     *
     * @author mjahnen
     */
    private static class CopyTaskParam {
        /* package */ UsbFile from;
        /* package */ File to;
    }

    /**
     * Asynchronous task to copy a file from the mass storage device connected
     * via USB to the internal storage.
     *
     * @author mjahnen
     */
    private class CopyTask extends AsyncTask<CopyTaskParam, Integer, Void> {

        //        private ProgressDialog dialog;
        private CopyTaskParam param;
        private FileSystem currentFs;

        public CopyTask(FileSystem currentFs) {
            this.currentFs = currentFs;
//            dialog = new ProgressDialog(ItemListActivity.this);
//            dialog.setTitle("Copying file");
//            dialog.setMessage("Copying a file to the internal storage, this can take some time!");
//            dialog.setIndeterminate(false);
//            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected void onPreExecute() {
//            dialog.show();
        }

        @Override
        protected Void doInBackground(CopyTaskParam... params) {
            param = params[0];
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(param.to));
                InputStream inputStream =
                        UsbFileStreamFactory.createBufferedInputStream(param.from, currentFs);
                byte[] bytes = new byte[4096];
                int count;
                int total = 0;

                while ((count = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                    total += count;
                    publishProgress((int) total);
                }

                out.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            dialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            dialog.setMax((int) param.from.getLength());
//            dialog.setProgress(values[0]);
        }

    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                    if (device != null) {
                        //setupDevice();
                    }
                } else {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    PendingIntent permissionIntent =
                            PendingIntent.getBroadcast(ItemListActivity.this, 0, new Intent(
                                    ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, permissionIntent);
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                // determine if connected device is a mass storage devuce
                if (device != null) {
                    discoverDevice();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);


                // determine if connected device is a mass storage devuce
                if (device != null) {
                    // check if there are other devices or set action bar title
                    // to no device if not
                    discoverDevice();
                }
            }

        }
    };

    /**
     * Searches for connected mass storage devices, and initializes them if it
     * could find some.
     */
    private void discoverDevice() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this);

        for (UsbMassStorageDevice device :
                devices) {
            UsbDevice usbDevice = getIntent().
                    getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (usbDevice != null && usbManager.hasPermission(usbDevice)) {
                try {
                    device.init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // first request permission from user to communicate with the
                // underlying
                // UsbDevice
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                        ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(device.getUsbDevice(), permissionIntent);
            }
        }

    }
}
