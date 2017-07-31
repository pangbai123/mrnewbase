package com.mrnew.core.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;

/**
 * Created by pb on 2017/4/5.
 */
public class FileUtils {
    /**
     * 压缩图片,并返回临时文件地址
     *
     * @param uri       本地文件地址
     * @param maxWidth  压缩最大宽度
     * @param maxHeight 压缩最大宽度
     * @param maxSize   压缩最大内存,单位kb
     * @return 临时文件地址
     */
    public static String compressImage(Context context, String uri, int maxWidth, int maxHeight, int maxSize) throws OutOfMemoryError, IOException {
        Bitmap image = getBitmapFromFile(new File(uri), maxWidth, maxHeight);
        if (image == null) {
            throw new IOException();
        }
        Bitmap.CompressFormat format;
        String suffix = getFileSuffix(uri);
        if (suffix != null && suffix.equalsIgnoreCase("png")) {
            format = Bitmap.CompressFormat.PNG;
        } else {
            format = Bitmap.CompressFormat.JPEG;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(format, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxSize && options > 20) {
            baos.reset();
            image.compress(format, options, baos);
            options -= 10;
        }
        return saveTempBitmap(context, baos, format);
    }

    /**
     * 获取压缩后的图片
     *
     * @param file
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap getBitmapFromFile(File file, int maxWidth, int maxHeight) throws IOException, OutOfMemoryError {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException();
        }
        String suffix = getFileSuffix(file.getAbsolutePath());
        Bitmap.Config format;
        if (suffix != null && suffix.equalsIgnoreCase("png")) {
            format = Bitmap.Config.ARGB_4444;
        } else {
            format = Bitmap.Config.RGB_565;
        }
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
            opts.inPreferredConfig = format;
        }
        return BitmapFactory.decodeFile(file.getPath(), opts);
    }

    /**
     * 计算缩放比例
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
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

    /**
     * 计算缩放比例
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
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
     * @param format
     * @return
     * @throws IOException
     */
    public static String saveTempBitmap(Context context, ByteArrayOutputStream baos, Bitmap.CompressFormat format) throws IOException {
        String fileName;
        if (format != null && format == Bitmap.CompressFormat.PNG) {
            fileName = "temp" + Utils.getUUID() + ".png";
        } else {
            fileName = "temp" + Utils.getUUID() + ".jpg";

        }
        String path = context.getExternalCacheDir().getAbsolutePath();
        String filePath = path + "/" + fileName;
        return saveBitmap(baos, filePath, true);
    }

    /**
     * 保存图片
     *
     * @param baos
     * @param toPath
     * @param isRewrite
     * @return
     * @throws IOException
     */
    public static String saveBitmap(ByteArrayOutputStream baos, String toPath, boolean isRewrite) throws IOException {
        if (baos == null) {
            throw new IOException();
        } else {
            if (TextUtils.isEmpty(toPath) || toPath.endsWith("/")) {
                throw new IOException();
            }
            File destFile = new File(toPath);
            if (destFile.exists()) {
                if (isRewrite) {
                    destFile.delete();
                } else {
                    return toPath;
                }
            }
            destFile.createNewFile();
            OutputStream os = new FileOutputStream(destFile);
            os.write(baos.toByteArray());
            os.flush();
            os.close();
        }
        return toPath;
    }


    /**
     * 保存图片
     *
     * @param bitmap
     * @param toPath
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Bitmap bitmap, String toPath, boolean isRewrite) throws IOException {
        if (bitmap == null) {
            throw new IOException();
        }
        if (TextUtils.isEmpty(toPath) || toPath.endsWith("/")) {
            throw new IOException();
        }
        File destFile = new File(toPath);
        if (destFile.exists()) {
            if (isRewrite) {
                destFile.delete();
            } else {
                return toPath;
            }
        }
        destFile.createNewFile();
        OutputStream os = new FileOutputStream(destFile);
        String suffix = getFileSuffix(toPath);
        if (suffix != null && suffix.equalsIgnoreCase("png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        }

        os.flush();
        os.close();
        return toPath;
    }

    /**
     * 删除缓存图片，默认路径为getExternalCacheDir
     *
     * @param context
     * @param path    为null，为全部删除
     */
    public static void deleteTempFile(final Context context, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = context.getExternalCacheDir();
                File[] files = dir.listFiles();
                for (File file : files) {
                    try {
                        File item = file;
                        if (item.exists() && !item.isDirectory()) {
                            if (path == null) {
                                if (item.getName().startsWith("temp")) {
                                    item.delete();
                                }
                            } else {
                                if (path.equals(item.getAbsolutePath())) {
                                    item.delete();
                                    return;
                                }
                            }
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
    public static void copyfile(File fromFile, File toFile, Boolean rewrite) throws Exception {
        if (!fromFile.exists()) {
            throw new IOException();
        }
        if (!fromFile.isFile()) {
            throw new IOException();
        }
        if (!fromFile.canRead()) {
            throw new IOException();
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists()) {
            if (rewrite) {
                toFile.delete();
            } else {
                return;
            }
        }
        toFile.createNewFile();
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        Exception rete = null;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }

        } catch (Exception ex) {
            rete = ex;
            Log.e("readfile", ex.getMessage());

        } finally {
            try {
                fosfrom.close();
                fosto.close();
            } catch (IOException e) {
                throw e;
            }
        }
        if (rete != null) {
            throw rete;
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
        String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        return picturePath + "/";
    }

    /**
     * 获取文件后缀
     *
     * @param path
     * @return
     */
    public static String getFileSuffix(String path) {
        String suffix = null;
        if (path != null) {
            int index = path.lastIndexOf("/");
            if (index != -1) {
                String name = path.substring(index + 1);
                int suffixindex = name.lastIndexOf(".");
                if (suffixindex != -1) {
                    suffix = name.substring(suffixindex + 1);
                }
            }

        }
        return suffix;
    }

    /**
     * 从相册获取文件
     *
     * @param context
     * @param uri
     * @return null，为获取失败
     */
    public static File getFileByUri(Context context, Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            return new File(path);
        } else {
            Log.i("videouri", "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }
}
