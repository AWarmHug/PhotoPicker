package com.warm.picker.zip.work;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.warm.picker.pick.filter.MimeType;
import com.warm.picker.zip.entity.CompressInfo;

import java.io.File;
import java.io.IOException;

/**
 * 作者：warm
 * 时间：2018-08-05 12:09
 * 描述：
 */
public abstract class Compressor {

    public static final int WIDTH = 1080;


    public String compress(CompressInfo compressInfo) {

        File souFile = new File(compressInfo.fromPath);
        if (souFile.length() <= compressInfo.filterSize) {
            return compressInfo.fromPath;
        }

        int degree;
        try {
            ExifInterface exif = new ExifInterface(compressInfo.fromPath);
            int rotate = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (rotate) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;

                default:
                    degree = ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            degree = 0;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(compressInfo.fromPath, options);

        if (options.outMimeType.equals(MimeType.GIF)) {
            return compressInfo.fromPath;
        }

        int width, height;
        if (compressInfo.width != 0 && compressInfo.height != 0) {
            width = (degree == 90 || degree == 270) ? compressInfo.height : compressInfo.width;
            height = (degree == 90 || degree == 270) ? compressInfo.width : compressInfo.height;
        } else {
            width = (degree == 90 || degree == 270) ? options.outHeight : options.outWidth;
            height = (degree == 90 || degree == 270) ? options.outWidth : options.outHeight;
            if (width > WIDTH) {
                height *= WIDTH * 1f / width;
                width = WIDTH;
            }
        }
        if (degree != 0) {
            options.inSampleSize = getSampleSize(options.outHeight, options.outWidth, width, height);
        } else {
            options.inSampleSize = getSampleSize(options.outWidth, options.outHeight, width, height);
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(compressInfo.fromPath, options);

        if (degree != 0) {
            bitmap = rotateBitmap(bitmap, degree);
        }

        File file = new File(compressInfo.toPath);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return compressInfo.fromPath;
        }

        if (file.length() != 0) {
            return compressInfo.toPath;
        }

        comeOn(bitmap, compressInfo.toPath);

        bitmap.recycle();

        if (file.length() != 0 && file.length() < souFile.length()) {
            return compressInfo.toPath;
        } else {
            return compressInfo.fromPath;
        }
    }


    /**
     * 根据图片的宽高,和imageview的宽高,计算出来的压缩比例
     *
     * @return
     */
    private int getSampleSize(int realWidth, int realHeight, int width, int height) {
        if (width == 0 || height == 0) {
            return 1;
        }

        if (realWidth > width || realHeight > height) {
            //需要进行压缩;
            int widthSize = Math.round(realHeight / height);
            int heightSize = Math.round(realWidth / width);

            int size = Math.min(widthSize, heightSize);
            return size % 2 == 0 ? size : size - 1;
        }
        return 1;
    }


    public Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public abstract boolean comeOn(Bitmap bitmap, String toPath);

}
