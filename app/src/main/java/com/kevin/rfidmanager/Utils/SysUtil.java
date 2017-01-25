package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.TelephonyManager;

import java.util.Calendar;
import java.util.Date;

import static java.text.DateFormat.getTimeInstance;

/**
 * This class is not needed now, but may used in future. Ignore this util using it.
 * 获取系统信息的工具类
 * Created by Kevin on 2017/1/26
 */
public class SysUtil {
    /**
     * 获取手机IMEI号
     *
     * @param context 上下文
     * @return 手机IMEI
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "未知IMEI";
        }
    }

    /**
     * 获取时间戳
     * @return 时间戳
     */
    public static long getTime(){
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    /**
     * 格式化时间戳
     * @param TimeinMilliSeccond 时间戳
     * @return 格式化的时间
     */
    public static String formattedTime(long TimeinMilliSeccond){
        return getTimeInstance().format(new Date(TimeinMilliSeccond));
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其debug属性时:<br/>
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * @param context
     * @return
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }
}
