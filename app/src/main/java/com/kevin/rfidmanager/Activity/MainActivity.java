package com.kevin.rfidmanager.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kevin.rfidmanager.Adapter.PagerAdapter;
import com.kevin.rfidmanager.Fragments.ItemDetailFrag;
import com.kevin.rfidmanager.Fragments.ItemsEditFrag;
import com.kevin.rfidmanager.Fragments.ItemsListFrag;
import com.kevin.rfidmanager.Fragments.SettingFrag;
import com.kevin.rfidmanager.MyApplication;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.ConstantManager;
import com.kevin.rfidmanager.Utils.DatabaseUtil;
import com.kevin.rfidmanager.Utils.ExitApplication;
import com.kevin.rfidmanager.Utils.NonSwipeableViewPager;
import com.kevin.rfidmanager.Utils.SPUtil;

/*
The main page of RFID system.
 */
public class MainActivity extends AppCompatActivity {
    FloatingActionButton addButton;
    public NonSwipeableViewPager viewPager;
    public PagerAdapter adapter;
    public TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Detail"));
        tabLayout.addTab(tabLayout.newTab().setText("Edit"));
        tabLayout.addTab(tabLayout.newTab().setText("Setting"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Out"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        viewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//
//                return true;
//            }
//        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(ConstantManager.HOME, false);
                        break;
                    case 1:
                        if (((MyApplication) MainActivity.this.getApplication()).getCurrentItemID()
                                == ConstantManager.DEFAULT_RFID){
                            Toast.makeText(getApplicationContext(), "Please select item first.", Toast.LENGTH_LONG).show();
                        }else {
                            adapter.tab2.refreshUI();
                            viewPager.setCurrentItem(ConstantManager.DETAIL, false);
                        }
                        break;
                    case 2:
                        if (((MyApplication) MainActivity.this.getApplication()).getCurrentItemID()
                                == ConstantManager.DEFAULT_RFID){
                            Toast.makeText(getApplicationContext(), "Please select item first.", Toast.LENGTH_LONG).show();
                        }else {
                            adapter.tab3.refreshUI();
                            viewPager.setCurrentItem(ConstantManager.EDIT, false);
                        }
                        break;
                    case 3:
                        viewPager.setCurrentItem(ConstantManager.SETTING, false);
                        break;
                    case 4:
                        SPUtil.getInstence(getApplicationContext()).saveNeedPassword(true);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    default:
                        Log.e(getClass().getName(), "Wrong tab number");
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.exit_warning);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitApplication.getInstance().exit();
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
            // TODO enable the on key down method in each fragment
            exit();
            return true;
        }
        return super.onKeyUp(keyCode, event);

    }
}
