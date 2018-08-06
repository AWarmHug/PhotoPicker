package com.effective.bitmap.utils;

import android.graphics.Bitmap;

/**
 * Crop https://github.com/zengfw/EffectiveBitmap/blob/master/app/src/main/java/com/effective/bitmap/utils/EffectiveBitmapUtils.java
 */

public class EffectiveBitmapUtils {

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("effective-bitmap");
    }

    public static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes,
                                               boolean optimize);

    private static int DEFAULT_QUALITY = 30;

    public static String compressByJNI(Bitmap bit, String fileName, boolean optimize) {
        return compressByJNI(bit, DEFAULT_QUALITY, fileName, optimize);
    }

    public static String compressByJNI(Bitmap bit, int quality, String fileName, boolean optimize) {
        return compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
    }


}
