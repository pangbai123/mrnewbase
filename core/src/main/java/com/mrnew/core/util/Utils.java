package com.mrnew.core.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by mrnew on 2016/4/16.
 * email:474923660@qq.com
 * 普通通用工具类
 */
public class Utils {
    /**
     * 获取当前版本标示号
     *
     * @param mContext
     * @return
     */
    public static int getCurrentVersionCode(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前版本号
     *
     * @param mContext
     * @return
     */
    public static String getCurrentVersionName(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 拨打电话
     *
     * @param activity
     * @param phone
     */
    public static void callPhone(Activity activity, String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        activity.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param activity
     * @param phone
     * @param msg
     */
    public static void sendMessage(Activity activity, String phone, String msg) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        activity.startActivity(intent);
    }

    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        s = s.replace("-", "");
        s = s.toLowerCase();
        return s;
    }

    /**
     * 数字转换为大写中文
     *
     * @param src
     * @return
     */
    public static String numberToChinese(int src) {
        String res = null;
        switch (src) {
            case 0:
                res = "零";
                break;
            case 1:
                res = "一";
                break;
            case 2:
                res = "二";
                break;
            case 3:
                res = "三";
                break;
            case 4:
                res = "四";
                break;
            case 5:
                res = "五";
                break;
            case 6:
                res = "六";
                break;
            case 7:
                res = "七";
                break;
            case 8:
                res = "八";
                break;
            case 9:
                res = "九";
                break;
            case 10:
                res = "十";
                break;
            case 11:
                res = "十一";
                break;
            case 12:
                res = "十二";
                break;
            default:
                break;
        }
        return res;
    }

    /**
     * 获取设备唯一标识
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            if (!TextUtils.isEmpty(DEVICE_ID)) {
                return DEVICE_ID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String SerialNumber = Build.SERIAL;
        if (!TextUtils.isEmpty(SerialNumber)) {
            return SerialNumber;
        }
        return getUUID();
    }

    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @return
     */
    public static boolean copy(Context context, String content) {
        try {
            if (content == null) {
                content = "";
            }
            //android 3.0以上支持图文复制,目前只做文字复制
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("lable", content);
                cmb.setPrimaryClip(clip);
            } else {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(content);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
