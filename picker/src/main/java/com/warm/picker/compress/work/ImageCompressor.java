package com.warm.picker.compress.work;

import android.graphics.Bitmap;

import com.warm.picker.compress.CompressUtils;


/**
 * 作者：warm
 * 时间：2018-08-22 14:44
 * 描述：
 */
public class ImageCompressor extends Compressor {
    private static int DEFAULT_QUALITY = 30;

    @Override
    public boolean comeOn(Bitmap bitmap, String toPath) {
        return compressByJNI(bitmap, toPath, true).equals("1");
    }

    public static String compressByJNI(Bitmap bit, String fileName, boolean optimize) {
        return saveBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
    }

    private static String saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
        return CompressUtils.compress(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
    }
}
