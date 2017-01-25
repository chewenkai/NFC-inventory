package com.kevin.rfidmanager.Utils;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;
/**
 * Created by Kevin on 2017/1/26.
 */


public class ExitApplication extends Application {
    @SuppressWarnings("rawtypes")
    private List activityList = new LinkedList();
    private static ExitApplication instance;
    private ExitApplication() {
    }
    //单例模式中获取唯一的ExitApplication实例
    public static ExitApplication getInstance() {
        if (null == instance) {
            instance = new ExitApplication();
        }
        return instance;
    }
    //添加Activity到容器中
    @SuppressWarnings("unchecked")
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    //遍历所有Activity并finish
    public void exit() {
        //返回主界面的INTENT，和按HOME键的效果一样
//        Intent goHomeIntent = new Intent("android.intent.action.MAIN");
//        goHomeIntent.addCategory("android.intent.category.HOME");
//        startActivity(goHomeIntent);
        for (int i = 0; i < activityList.size(); i++)//Activity activity: activityList
        {
            Activity activity = (Activity) activityList.get(i);
            activity.finish();
        }
        System.exit(0);
    }
}