package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.location.LocationManager;


/**
 * This class is not needed now, but may used in future. Ignore this util using it.
 * Created by Kevin on 2017/1/26
 */

public class LocationUtil {
    /**
     * 判断GPS是否开启
     *
     * @param context
     * @return
     */
    public static boolean isGPSProviderOpen(final Context context) {
        LocationManager locationManager = (LocationManager
                ) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络定位是否开启
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkProviderOpen(final Context context) {
        LocationManager locationManager = (LocationManager
                ) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network) {
            return true;
        }
        return false;
    }


}
