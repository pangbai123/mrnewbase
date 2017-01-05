package com.mrnew.core.util;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrnew on 2017/1/5.
 * email:474923660@qq.com
 * Activity序列管理工具
 */
public class ActivityManager {
    private static ActivityManager instance;
    private List<Activity> activityList = new LinkedList<Activity>();

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (instance == null) instance = new ActivityManager();
        return instance;
    }

    /**
     * 获取当前最上层Acitivity
     *
     * @return
     */
    public Activity getCurrentActivity() {
        return activityList.get(activityList.size() - 1);
    }

    /**
     * 获取序列列表
     *
     * @return
     */
    public List<Activity> getActivityList() {
        return activityList;
    }

    /**
     * 将Acitivty加入序列尾部
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 从序列中移除Activity
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }
}
