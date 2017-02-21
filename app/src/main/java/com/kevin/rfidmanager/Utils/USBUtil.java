package com.kevin.rfidmanager.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Build;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.kevin.rfidmanager.Entity.DeviceFile;
import com.kevin.rfidmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 17-2-21.
 * Mail: chewenkaich@gmail.com
 */

public class USBUtil {

    /**
     * Get internal storage and usb storage list.
     *
     * @param activity activity of app
     * @return DeviceFile List
     */
    public static List<DeviceFile> getDevicePathSet(Activity activity) {
        // Detect primary storage and secondary storage
        List<DeviceFile> pathList = new ArrayList<>();
        Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
        File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
        File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
        if (sdCard != null) {
            DeviceFile deviceFile = new DeviceFile();
            deviceFile.defaultFile = sdCard.getPath();
            deviceFile.type = ConstantManager.DEFAULT_FILE;
            deviceFile.deviceName = activity.getString(R.string.internal_sd_card);
            pathList.add(deviceFile);
        } else if (externalSdCard != null) {
            DeviceFile deviceFile = new DeviceFile();
            deviceFile.defaultFile = externalSdCard.getPath();
            deviceFile.type = ConstantManager.DEFAULT_FILE;
            deviceFile.deviceName = activity.getString(R.string.external_sd_card);
            pathList.add(deviceFile);
        }

        // Detect USB devices
        int counter = 1;
        UsbManager mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.
                getMassStorageDevices(activity);
        for (UsbMassStorageDevice device : devices) {

            // before interacting with a device you need to call init()!
            try {
                if (mUsbManager.hasPermission(device.getUsbDevice())) {
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
                } else {
                    PendingIntent permissionIntent = PendingIntent.
                            getBroadcast(activity, 0, new Intent(ConstantManager.ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(device.getUsbDevice(), permissionIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return pathList;
    }
}
