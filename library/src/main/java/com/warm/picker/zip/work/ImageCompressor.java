package com.warm.picker.zip.work;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.warm.picker.crop.CropUtil;
import com.warm.picker.zip.entity.CompressInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：warm
 * 时间：2017-11-15 16:29
 * 描述：
 */

public class ImageCompressor implements Compressor {
    private static final String TAG = "ImageCompressor";

    private static final ImageCompressor IMAGE_COMPRESSOR = new ImageCompressor();

    public static ImageCompressor getInstance() {
        return IMAGE_COMPRESSOR;
    }

    private ImageCompressor() {
    }

    /**
     * 循环压缩图片 循环压缩剪切生成的图片
     *
     * @param compressInfo 压缩信息
     * @return 返回压缩后的地址 和 cropPath一样
     */
    @Override
    @WorkerThread
    public String compress(CompressInfo compressInfo) {
        //判断原文件体积是否小于要求大小，true ：return
        File souFile = new File(compressInfo.fromPath);
        if (souFile.length() <= compressInfo.maxSize) {
            return compressInfo.fromPath;
        }
        //压缩和显示图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //负责加载图片但是不保存到内存中,
        options.inJustDecodeBounds = true;
        //设置图片质量
        BitmapFactory.decodeFile(compressInfo.fromPath, options);
        options.inSampleSize = getSampleSize(options, compressInfo.width, compressInfo.height);
        options.inJustDecodeBounds = false;

        Bitmap.CompressFormat format = getCompressFormat(options);

        Bitmap bitmap = BitmapFactory.decodeFile(compressInfo.fromPath, options);
        if (format != Bitmap.CompressFormat.WEBP && format != Bitmap.CompressFormat.PNG) {
            int degree = CropUtil.getExifRotation(compressInfo.fromPath);
            if (degree != 0) {
                bitmap = rotateBitmap(bitmap, degree);
            }
        }
        File file = new File(compressInfo.toPath);
        int quality = 90;
        while ((compressInfo.maxSize < file.length() || file.length() == 0) && quality >= 30 && saveOutput(file, bitmap, quality, format)) {
            Log.d(TAG, "compress: name=" + file.getName() + "quality=" + quality);
            quality -= 10;
        }
        bitmap.recycle();
        if (file.length() < souFile.length()) {
            return compressInfo.toPath;
        } else {
            return compressInfo.fromPath;
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


}
