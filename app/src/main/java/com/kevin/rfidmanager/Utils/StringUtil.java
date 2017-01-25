package com.kevin.rfidmanager.Utils;

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
}
