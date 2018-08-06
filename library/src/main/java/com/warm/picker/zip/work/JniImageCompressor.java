package com.warm.picker.zip.work;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.effective.bitmap.utils.EffectiveBitmapUtils;
import com.warm.picker.zip.entity.CompressInfo;

import java.io.File;

/**
 * 作者：warm
 * 时间：2018-08-06 11:23
 * 描述：
 */
public class JniImageCompressor implements Compressor {

    @Override
    public String compress(CompressInfo compressInfo) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(compressInfo.fromPath, options);
        File file = new File(compressInfo.toPath);

        int quality = 90;

        while ((compressInfo.maxSize < file.length() || file.length() == 0) && quality >= 30) {
            EffectiveBitmapUtils.compressByJNI(bitmap, compressInfo.toPath, true);
            quality -= 10;
        }
        bitmap.recycle();
        return compressInfo.toPath;
    }

}
