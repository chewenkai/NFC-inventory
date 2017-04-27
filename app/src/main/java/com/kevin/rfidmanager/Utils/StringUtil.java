package com.kevin.rfidmanager.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is not needed now, but may used in future. Ignore this util using it.
 * 字符串工具类
 * Created by Kevin on 2017/1/26
 */
public class StringUtil {
    /**
     * 检查字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean regexMatch(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Bitmap getThumbnailBitmap(Activity activity, Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };

        // This method was deprecated in API level 11
        // Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(activity, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        cursor.moveToFirst();
        long imageId = cursor.getLong(column_index);
        //cursor.close();

        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                activity.getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null );

        return bitmap;
    }

}
