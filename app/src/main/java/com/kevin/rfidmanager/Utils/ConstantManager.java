package com.kevin.rfidmanager.Utils;

import android.util.DisplayMetrics;


/**
 * Define all constants we need in this project.
 * Created by Kevin on 2017/1/26
 */

public class ConstantManager {
    public static boolean IS_DEBUGING = false;

    // Main page
    public static int HOME = 0;
    public static int DETAIL = 1;
    public static int EDIT = 2;
    public static int SETTING = 3;
    public static int REQUEST_MAIN_IMAGE_FILE = 5;
    public static int REQUEST_GALLERY_IMAGE_FILE = 6;
    public static int PERMISSION_REQUEST_CODE=7;
    public static int DEFAULT_RFID = -1;

    // General
    public static int DEFAULT_IMAGE_WIDTH = 300;
    public static int DEFAULT_IMAGE_HEIGHT = 300;
    public static int MAIN_IMAGE_WIDTH_DP = 250;
    public static int DEFAULT_IMAGE_WIDTH_DP = 120;
    public static int DEFAULT_IMAGE_HEIGHT_DP = 120;

    // Extra name in PhotoActivity
    public static String INTENT_STRING_EXTRA_FILE_PATH = "image_view_file_path_extra";
    public static String GALLERY_CLICK_POSITION = "click_position";
}
