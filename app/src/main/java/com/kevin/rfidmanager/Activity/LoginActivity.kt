package com.kevin.rfidmanager.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.kevin.rfidmanager.MyApplication
import com.kevin.rfidmanager.R
import com.kevin.rfidmanager.Utils.ConstantManager
import com.kevin.rfidmanager.Utils.ConstantManager.*
import com.kevin.rfidmanager.Utils.DatabaseUtil
import com.kevin.rfidmanager.Utils.SPUtil
import com.kevin.rfidmanager.Utils.StringUtil
import com.kevin.rfidmanager.database.UsersDao
import org.jetbrains.anko.toast


/**
 * Created by kevin on 17-4-5.
 * Mail: chewenkaich@gmail.com
 */
class LoginActivity : AppCompatActivity() {
    private var mPersonEdit: EditText? = null   // user name editor
    private var mPwdEdit: EditText? = null  // Password text editor
    private var mLoginBtn: Button? = null
    private var mRegisterButton: Button? = null   // Login button
    private var mUserNameStr: String? = null
    private var mPwdStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val actionBar = supportActionBar
        actionBar!!.hide()   // Hide ActionBar
        checkPermission()
        findView()
        initView()

    }

    /**
     * This method will get a unique code of device.
     */
    private fun checkUUID(): Boolean {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        /*
        * getDeviceId() returns the unique device ID.
        * For example,the IMEI for GSM and the MEID or ESN for CDMA phones.
        */
        var deviceId = telephonyManager.deviceId
        if (deviceId == null) {
            deviceId = Settings.System.getString(contentResolver, Settings.System.ANDROID_ID)
        }
//        toast("Your unique code:" + deviceId)  /// This code will make your device show an message of Unique ID
//        if (deviceId != ConstantManager.UniqueCode) {
//            toast("This device is not supported.")
//            finish()
//            return false
//        }
        return true
    }

    /**
     *refer to Android 6.0 Changes.

        To provide users with greater data protection, starting in this release,
    Android removes programmatic access to the device’s local hardware identifier
    for apps using the Wi-Fi and Bluetooth APIs. The WifiInfo.getMacAddress()
    and the BluetoothAdapter.getAddress() methods now return a constant value of 02:00:00:00:00:00.
     */
    private fun checkWifiMacAddress() {
        val manager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        val address = info.macAddress
//        toast(address)
//        toast(getMacAddr())
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PHONE_STAT_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    if (checkUUID())
                        checkIsNeedPassword()  // If do not need password, go to main page directly.
                } else {

                }
                return
            }
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toast("permission granted")
                } else {
                    toast("RFID manager cannot run without granting permission")
                    finish()
                }
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    /*
     * find view in layout file.(xml file)
     */
    private fun findView() {
        mPersonEdit = findViewById(R.id.login_activity_personname_edittext) as EditText
        mPwdEdit = findViewById(R.id.login_activity_password_edittext) as EditText
        mLoginBtn = findViewById(R.id.login_activity_login_btn) as Button
        mRegisterButton = findViewById(R.id.login_activity_regist_btn) as Button
    }

    /*
     * init UI
     */
    private fun initView() {

        mLoginBtn!!.setOnClickListener(View.OnClickListener { v ->
            // The button is clicked.
            if (IS_DEBUGING) {
                startActivity(Intent(this@LoginActivity, ItemInventoryActivity::class.java))
                (application as MyApplication).currentUser = ConstantManager.DEFAULT_USER
                finish()
            }
            mUserNameStr = mPersonEdit!!.text.toString()
            if (StringUtil.isEmpty(mUserNameStr)) {
                Snackbar.make(v, R.string.empty_username_warning, Snackbar.LENGTH_LONG).show()
                return@OnClickListener
            }
            mPwdStr = mPwdEdit!!.text.toString()
            if (StringUtil.isEmpty(mPwdStr)) {
                Snackbar.make(v, R.string.empty_password_warning, Snackbar.LENGTH_LONG).show()
                return@OnClickListener
            }

            // compare two string
            val users = DatabaseUtil.queryUsers(this@LoginActivity, mUserNameStr)
            if (users.size == 0)
                Snackbar.make(v, R.string.user_not_exist, Snackbar.LENGTH_LONG).show()
            else {
                val user = users[0]
                if (mPwdStr == user.passWord) {
                    val intent = Intent(this@LoginActivity, ItemInventoryActivity::class.java)
                    intent.putExtra(ConstantManager.CURRENT_USER_NAME, user.userName)
                    startActivity(intent)
                    (application as MyApplication).currentUser = user.userName
                    finish()
                } else {
                    Snackbar.make(v, R.string.login_fail, Snackbar.LENGTH_LONG).show()
                }
            }

            packUpImm()
        })

        mRegisterButton!!.setOnClickListener { showPasswordInputDialog() }

        passwordReminder()
    }

    /*
    Remind user input password when first opening.
     */
    private fun passwordReminder() {
        // Are there any user info?
        val daoSession = (application as MyApplication).getmDaoSession()
        val usersDao = daoSession.usersDao
        val users = usersDao.queryBuilder().where(UsersDao.Properties.UserName.isNotNull).build().list()
        if (users.size == 0 && SPUtil.getInstence(applicationContext).needPassword!!) {
            // first time using this app
            showPasswordInputDialog()
        }
    }

    /*
    This is a dialog used for input new password when user have not set up any password.
     */
    fun showPasswordInputDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.password_input_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val usernameEdt = dialogView.findViewById(R.id.username_editor) as EditText
        val firstPasswordEdt = dialogView.findViewById(R.id.password_editor) as EditText
        val confirmPasswordEdt = dialogView.findViewById(R.id.confirm_password) as EditText
        val message = dialogView.findViewById(R.id.message_text_login) as TextView
        val checkBox = dialogView.findViewById(R.id.skip_pswd_checkbox) as CheckBox

        val saveButton = dialogView.findViewById(R.id.dialog_ok) as TextView
        val cancleButton = dialogView.findViewById(R.id.dialog_cancle) as TextView

        dialogBuilder.setTitle(R.string.welcome)
        dialogBuilder.setMessage(R.string.password_input_reminder)
        val b = dialogBuilder.create()
        b.show()

        saveButton.setOnClickListener(View.OnClickListener {
            // check CheckBox status
            if (checkBox.isChecked) {
                SPUtil.getInstence(applicationContext).saveNeedPassword(false)
                Toast.makeText(applicationContext, R.string.password_omitted, Toast.LENGTH_LONG).show()
                DatabaseUtil.addNewUser(this@LoginActivity, ConstantManager.DEFAULT_USER, "")
                val intent = Intent(this@LoginActivity, ItemInventoryActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_USER_NAME,
                        ConstantManager.DEFAULT_USER)
                startActivity(intent)
                (application as MyApplication).currentUser = ConstantManager.DEFAULT_USER
                b.dismiss()
                finish()
                return@OnClickListener
            }
            if (usernameEdt.text.toString().isEmpty()) {
                message.setText(R.string.empty_username_warning)
                message.setTextColor(resources.getColor(R.color.warning_color))
                return@OnClickListener
            }
            if (firstPasswordEdt.text.toString().isEmpty() || confirmPasswordEdt.text.toString().isEmpty()) {
                message.setText(R.string.empty_password_warning)
                message.setTextColor(resources.getColor(R.color.warning_color))
                return@OnClickListener
            }
            // check password of two text editors
            if (firstPasswordEdt.text.toString() != confirmPasswordEdt.text.toString()) {
                message.setText(R.string.diff_passwd)
                message.setTextColor(resources.getColor(R.color.warning_color))
                return@OnClickListener
            }
            //save password with edt.getText().toString();
            if (DatabaseUtil.addNewUser(this@LoginActivity, usernameEdt.text.toString(), firstPasswordEdt.text.toString())) {
                Toast.makeText(applicationContext, R.string.password_saved, Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginActivity, ItemInventoryActivity::class.java)
                intent.putExtra(ConstantManager.CURRENT_USER_NAME, usernameEdt.text.toString())
                startActivity(intent)
                (application as MyApplication).currentUser = usernameEdt.text.toString()
                finish()
            } else {
                message.setText(R.string.username_exist)
                message.setTextColor(resources.getColor(R.color.warning_color))
                return@OnClickListener
            }

            b.dismiss()
        })

        cancleButton.setOnClickListener {
            //Exit system
            b.dismiss()
        }
    }

    /*
    Check that if user need password to protect their information.
     */
    private fun checkIsNeedPassword() {
        if (!SPUtil.getInstence(applicationContext).needPassword) {
            val intent = Intent(this@LoginActivity, ItemInventoryActivity::class.java)
            intent.putExtra(ConstantManager.CURRENT_USER_NAME, ConstantManager.DEFAULT_USER)
            startActivity(intent)
            (application as MyApplication).currentUser = ConstantManager.DEFAULT_USER
            finish()
        }
    }

    private fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
        val phoneStatePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        if (phoneStatePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), PHONE_STAT_PERMISSION_REQUEST_CODE)
        } else {
            if (checkUUID())
                checkIsNeedPassword()  // If do not need password, go to main page directly.
        }
    }

    /*
    Hide input method
     */
    private fun packUpImm() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}