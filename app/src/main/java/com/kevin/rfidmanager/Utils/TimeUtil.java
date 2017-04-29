package com.kevin.rfidmanager.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static final String TIME_FORMAT_DEFAOUT = "yyyyMMddHHmmss";
    public static final String KEY_SEQUENCE = "local_sequence";

    /**
     * 获得当前系统时间
     *
     * @param timeformate 如"yyyyMMddHHmmss"
     * @return string
     */
    public static String getCurrentTime(String timeformate) {
        String time = null;
        time = new SimpleDateFormat(timeformate, Locale.getDefault()).format(new Date(System
                .currentTimeMillis()));
        return time;
    }

    /**
     * 将格式化的时间转化成millis
     *
     * @param timeformate 如"yyyyMMddHHmmss"
     * @return string
     */
    public static Long getMillisTime(String timeformate, SimpleDateFormat sdf) {
        try {
            Date date = sdf.parse(timeformate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * toast
     *
     * @param context current context
     * @param message for display
     */
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示指定路径的图片
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmap(String path, int sampleSize) {
        System.out.println("sign_path-->" + path);
        File file = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        System.out.println("file-->" + file.exists());
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(file.getPath(), options);
            System.out.println("bm-->" + bm);
            return bm;
        }
        return null;
    }

    /**
     * 将bitmap转换为字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {

        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * 將字符串转换为bitmap
     *
     * @param string
     * @return
     */
    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
