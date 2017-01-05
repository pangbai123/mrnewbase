package com.mrnew.core.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.*;

/**
 * Created by pb on 2014/12/29.
 */
public class FileUtils {
    /**
     * 压缩图片
     *
     * @param uri       本地文件地址
     * @param maxWidth  压缩最大宽度
     * @param maxHeight 压缩最大宽度
     * @param maxSize   压缩最大内存,单位kb
     * @return
     */
    public static String compressImage(Context context, String uri, int maxWidth, int maxHeight, int maxSize) {
        Bitmap image = getBitmapFromFile(new File(uri), maxWidth, maxHeight);
        if (image != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                int options = 100;
                while (baos.toByteArray().length / 1024 > maxSize && options > 20) {
                    baos.reset();
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 10;
                }
                return saveBitmap(context, baos);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取压缩后的图片
     *
     * @param file
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap getBitmapFromFile(File file, int maxWidth, int maxHeight) {
        if (null != file && file.exists()) {
            BitmapFactory.Options opts = null;
            if (maxWidth > 0 && maxHeight > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;//只读信息
                BitmapFactory.decodeFile(file.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(maxWidth, maxHeight);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, maxWidth * maxHeight);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;//使用16位图，不支持透明度
            }
            try {
                return BitmapFactory.decodeFile(file.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 保存缓存图片，默认路径为getExternalCacheDir
     *
     * @param baos
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Context context, ByteArrayOutputStream baos) throws IOException {
        String fileName = "temp" + Utils.getUUID() + ".jpg";
        String filePath = "";
        if (baos == null) {
            return filePath;
        } else {
            String path = context.getExternalCacheDir().getAbsolutePath();
            filePath = path + "/" + fileName;
            File destFile = new File(filePath);
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();
            OutputStream os = null;
            try {
                os = new FileOutputStream(destFile);
                os.write(baos.toByteArray());
                os.flush();
                os.close();
            } catch (IOException e) {
                filePath = "";
            }
        }
        return filePath;
    }

    /**
     * 保存缓存图片，默认路径为getExternalCacheDir
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Context context, Bitmap bitmap) throws IOException {
        String fileName = "temp" + Utils.getUUID() + ".jpg";
        String path = context.getExternalCacheDir().getAbsolutePath();
        String filePath = path + "/" + fileName;
        return saveBitmap(bitmap, filePath);
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Bitmap bitmap, String filePath) throws IOException {
        if (bitmap == null) {
            return "";
        }
        File destFile = new File(filePath);
        if (destFile.exists()) {
            destFile.delete();
        }
        destFile.createNewFile();
        OutputStream os = null;
        try {
            os = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            filePath = "";
        }
        return filePath;
    }

    /**
     * 删除缓存图片，默认路径为getExternalCacheDir
     */
    public static void deleteCacheFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = context.getExternalCacheDir();
                File[] files = dir.listFiles();
                for (File file : files) {
                    try {
                        File item = file;
                        if (item.exists() && !item.isDirectory() && item.getName().startsWith("temp")) {
                            item.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 复制文件
     *
     * @param fromFile
     * @param toFile
     * @param rewrite
     */
    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        FileInputStream fosfrom = null;

        FileOutputStream fosto = null;

        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //??????��???????????
            }

        } catch (Exception ex) {

            Log.e("readfile", ex.getMessage());

        } finally {
            try {
                fosfrom.close();
                fosto.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取目录下文件总大小
     *
     * @param path
     * @return
     */
    public static long getDirectorySize(String path) {
        long size = 0;
        try {
            File file = new File(path);
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size
                            + getDirectorySize(fileList[i].getAbsolutePath());
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    /**
     * 获取相册路径
     *
     * @return
     */
    public static String getSystemImagePath() {
        if (Build.VERSION.SDK_INT > 7) {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            return picturePath + "/my/";
        } else {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            return picturePath + "/my/";
        }
    }
}
