package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络工具类
 * Network Utils
 * Created by young4979 on 2016/11/16.
 */
public class NetWorkUtil {
    private static final String TAG = NetWorkUtil.class.getName();

    /**
     * 检查网络连接状态
     * Monitor network connections (Wi-Vi, GPRS, UMTS, etc.)
     *
     * @param context
     * @return
     */
    public static boolean checkNetWorkStatus(Context context) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
            Log.i(TAG, "The net was connected");
        } else {
            result = false;
            Log.i(TAG, "The net was bad!");
        }
        return result;
    }

    /**
     * check the type of Network
     * @param context
     * @return
     */
    public static boolean typeOfNetWorkIsWifi(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Global system settings, containing preferences that always apply identically to all defined users.
     * Applications can read these but are not allowed to write; like the "Secure" settings, these are for
     * preferences that the user must explicitly modify through the system UI or specialized APIs for
     * those values.
     * @param context
     * @param enabled
     */
    public static void setMobileConnectionDisabled(Context context, boolean enabled) {
        // Can Not Achieve Now.
    }

    /**
     * check the type of Network
     * @param context
     * @return
     */
    public static boolean typeOfNetWorkIsMobile(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 检查url是否有效
     *
     * @param url
     * @return
     */
    public static boolean checkURL(String url) {
        boolean value = false;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            int code = conn.getResponseCode();
            if (code != 200) {
                value = false;
            } else {
                value = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取总的接受字节数，包含Mobile和WiFi等
     *
     * @return
     */
    public static long getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
    }

    /**
     * 总的发送字节数，包含Mobile和WiFi等
     *
     * @return
     */
    public static long getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() / 1024);
    }

    /**
     * 获取通过Mobile连接收到的字节总数，不包含WiFi
     *
     * @return
     */
    public static long getMobileRxBytes() {
        return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileRxBytes() / 1024);
    }

    /**
     * 获取通过Mobile连接发送的字节总数，不包含WiFi
     *
     * @return
     */
    public static long getMobileTxBytes() {
        return TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileTxBytes() / 1024);
    }

    /**
     * Get this app's transmitted data
     * @param context
     * @return
     */
    public static long getBytesTransmitted(Context context) {
            return TrafficStats.getUidTxBytes(context.getApplicationInfo().uid);
        }

    /**
     * Get this app's received data
     * @param context
     * @return
     */
    public static  long getBytesReceived(Context context) {
            return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid);
        }
}
