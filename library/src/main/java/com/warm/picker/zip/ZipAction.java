package com.warm.picker.zip;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.warm.picker.crop.CropUtil;
import com.warm.picker.find.entity.Image;
import com.warm.picker.find.work.ImageFind;
import com.warm.picker.zip.bean.ZipInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：warm
 * 时间：2017-11-15 16:29
 * 描述：
 */

public class ZipAction {
    private static final String TAG = "ZipAction";

    private static final ZipAction zipAction = new ZipAction();

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
    public String zipImage(ZipInfo zipInfo) {
        //判断原文件体积是否小于要求大小，true ：return
        File souFile = new File(zipInfo.fromPath);
        if (souFile.length() <= zipInfo.maxSize) {
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
        if (format != Bitmap.CompressFormat.WEBP && format != Bitmap.CompressFormat.PNG) {
            int degree = CropUtil.getExifRotation(zipInfo.fromPath);
            if (degree != 0) {
                bitmap = rotateBitmap(bitmap, degree);
            }
        }
        File file = new File(zipInfo.toPath);
        int quality = 90;
        while ((zipInfo.maxSize < file.length() || file.length() == 0) && quality >= 30 && saveOutput(file, bitmap, quality, format)) {
            Log.d(TAG, "zipImage: name=" + file.getName() + "quality=" + quality);
            quality -= 10;
        }
        bitmap.recycle();
        if (file.length() < souFile.length()) {
            return zipInfo.toPath;
        } else {
            return zipInfo.fromPath;
        }
    }


    public Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 根据图片的宽高,和imageview的宽高,计算出来的压缩比例
     *
     * @param options
     * @return
     */
    public int getSampleSize(BitmapFactory.Options options, int width, int height) {
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

            int size = widthSize > heightSize ? widthSize : heightSize;
            return size % 2 == 0 ? size : size - 1;
        }
        return 1;
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
            case "webp":
                return Bitmap.CompressFormat.WEBP;
            default:
                return Bitmap.CompressFormat.JPEG;
        }
    }

    public boolean saveOutput(File file, Bitmap croppedImage, int quality, Bitmap.CompressFormat format) {
        Log.d(TAG, "saveOutput: quality=" + quality);


        if (croppedImage != null) {
            BufferedOutputStream bos = null;
            try {
                if (!file.exists()) {
                    if (!file.getParentFile().exists() && file.getParentFile().mkdirs()) {
                        file.createNewFile();
                    }
                }
                bos = new BufferedOutputStream(new FileOutputStream(file));
                croppedImage.compress(format, quality, bos);
                bos.flush();
            } catch (IOException e) {
                return false;
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
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
     * @return
     */
    @WorkerThread
    public Uri saveContentProvider(ContentResolver cr, File file, BitmapFactory.Options options, long time) {

        //判断是否该路径是否存在Uri，true：update；false：insert
        Image image = ImageFind.getInstance().findImageByPath(cr, file.getPath());
        if (image != null && image.getId() != 0) {
            return image.getUri();
        } else {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            long dateSeconds = time / 1000;

            // Save the screenshot to the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.ImageColumns.DATA, file.getPath());
            values.put(MediaStore.Images.ImageColumns.TITLE, file.getName());
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION, file.getName());
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
            return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }


}
