package com.kevin.rfidmanager.Entity;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.kevin.rfidmanager.Utils.ConstantManager;

/**
 * Created by kevin on 17-2-16.
 * Mail: chewenkaich@gmail.com
 */

public class DeviceFile {
    public String deviceName = "";  // Device name, shows in the list of dialog

    public String defaultFile = null;  // Non-USB device file

    public UsbFile usbFile = null;  // USB file
    public UsbMassStorageDevice device = null;  // USB device object

    public int type = ConstantManager.DEFAULT_FILE;  // File type
}
