package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * SharedPreferences Utils
 * Created by Kevin on 2017/1/26
 */
public class SPUtil {
    private static final String SP_NAME = "RFID_sp";    // SharedPreferences name
    private static final String FIRST_OPEN = "is_this_first_use";  // is this first use?
    private static final String PERSON_NAME = "person_name";  // user name
    private static final String PERSON_PWD = "person_pwd";  // password
    private static final String NEED_PASSWD = "need_passwd";  // is user need password to protect info?

    private static SPUtil instence;
    private SharedPreferences sharedPreferences;

    private SPUtil() throws Exception {
        throw new Exception("Forbid using this method to generate instance.");
    }

    private SPUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get instance
     * @param context
     * @return
     */
    public static SPUtil getInstence(Context context) {
        if (instence == null) {
            synchronized (SPUtil.class) {
                if (instence == null) {
                    instence = new SPUtil(context);
                }
            }
        }
        return instence;
    }

    public void savePersonName(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSON_NAME, value);
        editor.commit();
    }

    public String getPersonName() {
        String str = sharedPreferences.getString(PERSON_NAME, "");
        return str;
    }

    public void savePassWord(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSON_PWD, value);
        editor.commit();
    }

    public String getPassWord() {
        String str = sharedPreferences.getString(PERSON_PWD, "");
        return str;
    }

    public void saveFirstUseStatus(Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_OPEN, value);
        editor.commit();
    }

    public Boolean getFirstUseStatus() {
        boolean isFirstUse = sharedPreferences.getBoolean(FIRST_OPEN, true);
        return isFirstUse;
    }

    public void saveNeedPassword(Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NEED_PASSWD, value);
        editor.commit();
    }

    public Boolean getNeedPassword() {
        boolean isFirstUse = sharedPreferences.getBoolean(NEED_PASSWD, true);
        return isFirstUse;
    }

}
