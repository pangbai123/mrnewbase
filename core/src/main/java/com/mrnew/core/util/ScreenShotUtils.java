package com.mrnew.core.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * 进行截屏工具类
 *
 *  2013/09/29
 */
public class ScreenShotUtils {
    /**
     * 进行截取屏幕
     *
     * @param pActivity
     * @return bitmap
     */
    public static Bitmap takeScreenShot(Activity pActivity, int stautsHeight, int navigationBarHeight) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();
        // 设置是否可以进行绘图缓存
        view.setDrawingCacheEnabled(true);
        // 如果绘图缓存无法，强制构建绘图缓存
        view.buildDrawingCache();
        // 返回这个缓存视图
        bitmap = view.getDrawingCache();
        int width = UiUtils.getScreenWidth();
        int height = UiUtils.getScreenHeight();
        // 根据坐标点和需要的宽和高创建bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight - navigationBarHeight);
        return bitmap;
    }

    /**
     * 对View进行截图
     *
     * @param view
     */
    public static Bitmap shotView(View view) {
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
