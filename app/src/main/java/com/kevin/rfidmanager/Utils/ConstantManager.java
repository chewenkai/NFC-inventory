package com.kevin.rfidmanager.Utils;

/**
 * Define all constants we need in this project.
 * Created by Kevin on 2017/1/26
 */

public class ConstantManager {
//            public static String UniqueCode = "868030021837446";  // mi5's unique code
    public static String UniqueCode = "32febcde30525d88";  // Hooman's unique code
    public static boolean IS_DEBUGING = false;

    // Main page
    public static int REQUEST_MAIN_IMAGE_FILE = 5;
    public static int REQUEST_GALLERY_IMAGE_FILE = 6;
    public static int PERMISSION_REQUEST_CODE = 7;
    public static int PHONE_STAT_PERMISSION_REQUEST_CODE = 12;
    public static String DEFAULT_RFID = "-1";
    public static String DEFAULT_USER = "default user";

    // General
    public static int DEFAULT_IMAGE_HEIGHT_DP = 120;  // default image height of gallery images in
    public static String NEW_RFID_CARD_BROADCAST_ACTION = "NEW_CARDS_COMING";
    public static String NEW_RFID_CARD_KEY = "NEW_CARDS_KEY";

    // Edit page and Detail page

    // Apperance
    public static int LINEAR_LAYOUT = 8;
    public static int STAGGER_LAYOUT = 9;
    public static int ONE_ROW_LAYOUT = 10;
    public static int DETAIL_LAYOUT = 11;

    // File
    public final static int DEFAULT_FILE = 0;
    public final static int USB_FILE = 1;
    public final static String ACTION_USB_PERMISSION =
            "com.kevin.rfidmanager.USB_PERMISSION";

    // Extra
    public final static String CURRENT_USER_NAME = "current_user_name";
    public final static String CURRENT_ITEM_ID = "current_item_id";
    public final static String INTENT_STRING_EXTRA_FILE_PATH = "image_view_file_path_extra";  // Extra name in PhotoActivity
    public final static String GALLERY_CLICK_POSITION = "click_position";  // Extra name in PhotoActivity
    public final static String CART_ITEM_RFID = "cart_item_rfid";  // items in cart
    public final static String CHECKOUT_ITEMS = "checkout_items";
    public final static String CHECKOUT_ITEMS_ID = "checkout_items_id";
    public final static String CHECKOUT_ITEMS_COUNT_EXTRA = "checkout_items_count";
    public final static String PAY_SUCCESSFUL_BROADCAST = "com.pay.successful";
    public final static String CLEAR_CART_BROADCAST = "com.clear.cart";

    public final static String PAY_SUCCESSFUL = "pay_succ";


}
