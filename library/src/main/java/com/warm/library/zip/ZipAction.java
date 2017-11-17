package com.warm.library.zip;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.warm.library.find.bean.ImageBean;
import com.warm.library.find.work.ImageFind;
import com.warm.library.zip.bean.ZipInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 作者：warm
 * 时间：2017-11-15 16:29
 * 描述：
 */

public class ZipAction {
    private static final String TAG = "ZipAction";

    private static ZipAction zipAction = new ZipAction();

    public static ZipAction getInstance() {
        return zipAction;
    }

    private ZipAction() {
    }

    /**
     * 循环压缩图片 循环压缩剪切生成的图片
     *
     * @param zipInfo 压缩信息
     * @return 返回压缩后的地址 和 cropPath一样
     */
    @WorkerThread
    public String zipImage(ContentResolver cr, ZipInfo zipInfo) {
        //判断原文件体积是否小于要求大小，true ：return
        if (new File(zipInfo.fromPath).length() <= zipInfo.size) {
            return zipInfo.fromPath;
        }

        //压缩和显示图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //负责加载图片但是不保存到内存中,
        options.inJustDecodeBounds = true;
        //设置图片质量
        BitmapFactory.decodeFile(zipInfo.fromPath, options);
        options.inSampleSize = getSampleSize(options, zipInfo.width, zipInfo.height);
        options.inJustDecodeBounds = false;

        Bitmap.CompressFormat format = getCompressFormat(options);
        Bitmap bitmap = BitmapFactory.decodeFile(zipInfo.fromPath, options);
        File file = new File(zipInfo.toPath);

        saveOutput(file, bitmap, 100, format);

        int quality = 90;
        while (zipInfo.size < file.length() && quality >= 50) {
            Log.d(TAG, "zipImage: name=" + file.getName() + "quality=" + quality);
            saveOutput(file, bitmap, quality, format);
            quality -= 10;
        }
        saveContentProvider(cr, file, options);
        bitmap.recycle();
        return zipInfo.toPath;
    }

    public Bitmap.CompressFormat getCompressFormat(BitmapFactory.Options options) {
        String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(options.outMimeType);
        switch (type) {
            case "jpg":
            case "jpeg":
            case "jpe":
                return Bitmap.CompressFormat.JPEG;
            case "png":
                return Bitmap.CompressFormat.PNG;
            default:
                return Bitmap.CompressFormat.JPEG;
        }

    }

    public boolean saveOutput(File file, Bitmap croppedImage, int quality, Bitmap.CompressFormat format) {
        Log.d(TAG, "saveOutput: quality=" + quality);
        if (file != null) {
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                croppedImage.compress(format, quality, outputStream);
            } catch (FileNotFoundException e) {
                return false;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable t) {
                        // Do nothing
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 将压缩的图片信息添加到ContentProvider中，在后面会根据路径查找图片时会使用到
     *
     * @param cr
     * @param file
     * @param
     * @return
     */
    public Uri saveContentProvider(ContentResolver cr, File file, BitmapFactory.Options options) {
        return saveContentProvider(cr, file, options, System.currentTimeMillis());
    }

    /**
     * 将压缩的图片信息添加到ContentProvider中，在后面会根据路径查找图片时会使用到
     *
     * @param cr
     * @param file
     * @param
     * @return
     */
    @WorkerThread
    public Uri saveContentProvider(ContentResolver cr, File file, BitmapFactory.Options options, long time) {

        // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
        // for DATE_TAKEN
        long dateSeconds = time / 1000;

        // Save the screenshot to the MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.ImageColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.ImageColumns.TITLE, file.getName());
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getParentFile().getName());
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, time);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, options.outMimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            values.put(MediaStore.Images.ImageColumns.WIDTH, options.outWidth);
            values.put(MediaStore.Images.ImageColumns.HEIGHT, options.outHeight);
        }
        values.put(MediaStore.Images.ImageColumns.SIZE, file.length());

        //判断是否该路径是否存在Uri，true：update；false：insert
        String id = ImageFind.getInstance().findImageByPath(cr, file.getPath()).getId();
        if (id != null) {
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            cr.update(uri, values, null, null);
            return uri;
        } else {
            return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }


    /**
     * 根据图片的宽高,和imageview的宽高,计算出来的压缩比例
     *
     * @param options
     * @return
     */
    private int getSampleSize(BitmapFactory.Options options, int width, int height) {
        if (width == 0 || height == 0) {
            return 1;
        }
        int realWidth = options.outWidth;
        int realHeight = options.outHeight;
        int reqWidth;
        int reqHeight;
        if (realWidth > realHeight) {
            reqWidth = height;
            reqHeight = width;
        } else {
            reqWidth = width;
            reqHeight = height;
        }
        if (realWidth > reqWidth || realHeight > reqHeight) {
            //需要进行压缩;
            int widthSize = Math.round(realHeight / reqHeight);
            int heightSize = Math.round(realWidth / reqWidth);
            return widthSize > heightSize ? widthSize : heightSize;
        }
        return 1;
    }

    public interface ZipSingleCallBack {
        void onFinish(ImageBean imageBean);
    }

}
