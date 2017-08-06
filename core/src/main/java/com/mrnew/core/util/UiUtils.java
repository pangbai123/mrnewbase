package com.mrnew.core.util;

import android.app.Activity;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by mrnew on 2016/4/16.
 * email:474923660@qq.com
 * 界面相关工具类
 */
public class UiUtils {
    /**
     * dp转换为px
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue) {
        try {
            final float scale = ActivityManager.getInstance().getCurrentActivity().getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * px转换为dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = ActivityManager.getInstance().getCurrentActivity().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        Activity activity = ActivityManager.getInstance().getCurrentActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return metrics.widthPixels;
        }
        return 0;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        Activity activity = ActivityManager.getInstance().getCurrentActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return metrics.heightPixels;
        }
        return 0;
    }


    private static long lastClickTime;

    /**
     * 是否为连续快速点击，用于点击回调时判断
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 200) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 统计文字占用几行
     *
     * @param title
     * @param text
     * @return
     */
    public static int countLines(TextView title, String text) {
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        TextPaint mTextPaint = title.getPaint();
        int mTextViewWidth = (int) mTextPaint.measureText(text);
        double width = UiUtils.getScreenWidth() - UiUtils.dp2px(200);
        return (int) Math.ceil(mTextViewWidth / width);
    }


    /**
     * 处理String对象为null的情况
     *
     * @param src
     * @return
     */
    public static String handleNullValue(String src) {
        if (src == null) return "";
        return src;
    }

    /**
     * 判断事件是否在view范围内
     *
     * @param view
     * @param ev
     * @return
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        if (view == null) return false;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x || ev.getRawX() > (x + view.getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }


    /**
     * 等比缩放view
     *
     * @param view
     * @param picWidth
     * @param picHeight
     * @param toWidth
     */
    public static void uniformScaleView(View view, int picWidth, int picHeight, int toWidth) {
        ViewGroup.LayoutParams para = view.getLayoutParams();
        para.width = toWidth;
        para.height = picHeight * toWidth / picWidth;
        view.setLayoutParams(para);
    }

    /**
     * 本地加载富文本
     *
     * @param webView
     * @param bodyHTML
     */
    public static void loadLocalHtml(WebView webView, String bodyHTML) {
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        String ret = "";
        if (bodyHTML != null && bodyHTML.startsWith("<html")) {
            ret = bodyHTML;
        } else {
            String head = "<head>" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                    "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                    "</head>";
            ret = "<html>" + head + "<body>" + bodyHTML + "</body></html>";
        }
        webView.loadData(ret, "text/html; charset=utf-8", "utf-8");
    }
}
