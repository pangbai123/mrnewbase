package com.mrnew.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络情况相关
 */
public class NetworkUtil {
    /**
     * 判断是否连接网络
     *
     * @return
     */
    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static NetType getCurrentNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //已连接
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetType.TYPE_WIFI;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NetType.TYPE_MOBILE;
            } else {
                return NetType.TYPE_UNKNOWN;
            }
        } else {
            //无网
            return NetType.TYPE_NO_NET;
        }
    }

    public enum NetType {
        TYPE_WIFI, TYPE_MOBILE, TYPE_UNKNOWN, TYPE_NO_NET
    }
}
