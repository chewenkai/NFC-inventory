package com.kevin.rfidmanager.Activity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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

import com.kevin.rfidmanager.Adapter.ItemListAdaper;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.SPUtil;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import at.markushi.ui.CircleButton;

public class ItemListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemListAdaper itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_layout);
        initActionBar();
        initUI();
    }

    private void initActionBar(){
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
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0,0);

        BoomMenuButton leftBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_left_bmb);
//        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);

        leftBmb.setButtonEnum(ButtonEnum.Ham);
        leftBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_5);
        leftBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_5);

        HamButton.Builder changeAppearance = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        changeApperanceDialog();
                    }
                })
                .normalImageRes(R.drawable.ic_color_lens_white_48dp)
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
                .normalTextRes(R.string.change_rfid_range);
        leftBmb.addBuilder(change_rfid_range);

//        rightBmb.setButtonEnum(ButtonEnum.Ham);
//        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
//        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
//        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
//            rightBmb.addBuilder(BuilderManager.getHamButtonBuilder());
    }
    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_item_list);
        List<Items> items = DatabaseUtil.queryItems(ItemListActivity.this);

        if(((MyApplication) getApplication()).getCurrentItemID() == ConstantManager.DEFAULT_RFID && items.size() != 0)
            ((MyApplication) getApplication()).setCurrentItemID(items.get(0).getRfid());

        itemListAdapter = new ItemListAdaper(ItemListActivity.this, items);
        recyclerView.setAdapter(itemListAdapter);
        setRecyclerViewLayout();
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        setRecyclerViewLayout();
        super.onResume();
    }

    private void setRecyclerViewLayout() {
        switch (SPUtil.getInstence(ItemListActivity.this).getApperance()){
            case 8:  // ConstantManager.LINEAR_LAYOUT
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ItemListActivity.this, 3, GridLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
                break;
            case 9:  // ConstantManager.STAGGER_LAYOUT
                StaggeredGridLayoutManager staggeredGridLayoutManager =
                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
            case 10:  // ConstantManager.ONE_ROW_LAYOUT
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemListActivity.this, LinearLayoutManager.VERTICAL, false);
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

        final TextInputEditText itemID = (TextInputEditText) dialogView.findViewById(R.id.edit_key_des_text_editor);
        final TextInputEditText itemName = (TextInputEditText) dialogView.findViewById(R.id.item_name_edit);
        final Button saveButton = (Button) dialogView.findViewById(R.id.dialog_change);
        final Button cancleButton = (Button) dialogView.findViewById(R.id.dialog_cancle);

        dialogBuilder.setTitle("Just input a number as a ID of RFID card and a name of item");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long new_id=null;
                try {
                    new_id = Long.parseLong(itemID.getText().toString());
                }catch (NumberFormatException e){
                    Toast.makeText(ItemListActivity.this, "please input number as ID", Toast.LENGTH_LONG).show();
                    return;
                }
                // Are there any user info?
                DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
                ItemsDao itemsDao = daoSession.getItemsDao();
                List<Items> items = itemsDao.queryBuilder().where(ItemsDao.Properties.Rfid.eq(new_id)).build().list();
                if (items.size()>0){
                    Toast.makeText(ItemListActivity.this, "The ID card is exist, please change a ID", Toast.LENGTH_LONG).show();
                    return;
                }
                ((MyApplication) getApplication()).setCurrentItemID(Long.parseLong(itemID.getText().toString()));
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
        final TextView textView = (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final CircleButton linear_layout = (CircleButton) dialogView.findViewById(R.id.linear_layout);
        final CircleButton staggered_layout = (CircleButton) dialogView.findViewById(R.id.staggered_layout);
        final CircleButton one_row_layout = (CircleButton) dialogView.findViewById(R.id.one_row_layout);

        switch (SPUtil.getInstence(ItemListActivity.this).getApperance()){
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
                SPUtil.getInstence(ItemListActivity.this).setApperance(ConstantManager.LINEAR_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                initUI();
                b.dismiss();

            }
        });

        staggered_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(ItemListActivity.this).setApperance(ConstantManager.STAGGER_LAYOUT);
                ((MyApplication) getApplication()).toast(getString(R.string.apperance_updated));
                initUI();
                b.dismiss();
            }
        });

        one_row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.getInstence(ItemListActivity.this).setApperance(ConstantManager.ONE_ROW_LAYOUT);
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


                List<Users> users = DatabaseUtil.queryUsers(ItemListActivity.this, ((MyApplication) getApplication()).getUserName());
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
        final AlertDialog b = dialogBuilder.create();
        final TextView textView = (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final CircleButton internal_backup = (CircleButton) dialogView.findViewById(R.id.store_internal_space);
        final CircleButton sd_backup = (CircleButton) dialogView.findViewById(R.id.store_sd_space);

        internal_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFilesDir().canWrite()) {
                    try {
                        File currentDB = getDatabasePath(getString(R.string.database_name));

                        String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
                        File backupDB = new File(getFilesDir(), backupDBPath);
                        backupDB.createNewFile();
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        ((MyApplication) getApplication()).toast(getString(R.string.backup_internal_successful));
                        b.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        textView.setText(R.string.internal_memory_read_fail);
                    }
                } else
                    textView.setText(R.string.internal_memory_read_fail);


            }
        });

        sd_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    File sd = Environment.getExternalStorageDirectory();
                    try {

                        File currentDB = getDatabasePath(getString(R.string.database_name));
                        String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
                        File backupDB = new File(sd, backupDBPath);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        ((MyApplication) getApplication()).toast(getString(R.string.backup_successful));
                        b.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        textView.setText(R.string.no_tf);
                    }

                } else
                    textView.setText(R.string.no_tf);
            }
        });


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
        final AlertDialog b = dialogBuilder.create();
        final TextView textView = (TextView) dialogView.findViewById(R.id.backup_dialog_message);
        final CircleButton internal_restore = (CircleButton) dialogView.findViewById(R.id.store_internal_space);
        final CircleButton sd_restore = (CircleButton) dialogView.findViewById(R.id.store_sd_space);

        internal_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFilesDir().canWrite()) {
                    try {
                        String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
                        File backupDB = getDatabasePath(getString(R.string.database_name));
                        File currentDB = new File(getFilesDir(), backupDBPath);
                        if (!backupDB.exists()) {
                            textView.setText(R.string.no_backup_File_data);
                        }
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        ((MyApplication) getApplication()).setCurrentItemID(ConstantManager.DEFAULT_RFID);
                        ((MyApplication) getApplication()).toast(getString(R.string.restore_successful));
                        b.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        textView.setText(R.string.internal_memory_read_fail);
                    }
                } else
                    textView.setText(R.string.internal_memory_read_fail);

            }
        });

        sd_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        String backupDBPath = String.format("%s.bak", getString(R.string.database_name));
                        File backupDB = getDatabasePath(getString(R.string.database_name));
                        File currentDB = new File(sd, backupDBPath);

                        if (!backupDB.exists()) {
                            textView.setText(R.string.no_backup_File_TF);
                        }

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        ((MyApplication) getApplication()).setCurrentItemID(ConstantManager.DEFAULT_RFID);
                        ((MyApplication) getApplication()).toast(getString(R.string.restore_successful));
                        b.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        textView.setText(R.string.no_tf);
                    }

                } else
                    textView.setText(R.string.no_tf);
            }
        });

        b.show();

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

}
