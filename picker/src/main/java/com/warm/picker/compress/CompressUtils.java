package com.warm.picker.compress;

import android.graphics.Bitmap;

/**
 * 作者：warm
 * 时间：2018-08-22 17:26
 * 描述：crop from
 */
public class CompressUtils {

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("compress");
    }

    public static native String compress(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes,
                                               boolean optimize);

}
