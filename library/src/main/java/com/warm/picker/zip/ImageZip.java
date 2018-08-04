package com.warm.picker.zip;

import android.content.ContentResolver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.entity.Image;
import com.warm.picker.find.work.ImageFind;
import com.warm.picker.zip.bean.ZipInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 作者：warm
 * 时间：2017-10-18 09:38
 * 描述：
 */
public class ImageZip {
    private static final String TAG = "ImageZip";

    private static final ImageZip zip = new ImageZip();

    public static ImageZip getInstance() {
        return zip;
    }


    public void zipImages(final List<ZipInfo> zipInfos, final ZipCallBack callBack) {
        final List<String> images = new Vector<>(zipInfos.size());
        for (int i = 0; i < zipInfos.size(); i++) {
            final ZipInfo zipInfo = zipInfos.get(i);

            WorkExecutor.getInstance().runWorker(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: " + Thread.currentThread());
                    String path = ZipAction.getInstance().zipImage(zipInfo);
                    images.add(path);
                    if (images.size() == zipInfos.size()) {
                        postUi(images, callBack);
                    }
                }
            });

        }
    }

    public List<Image> zipImage(final List<ZipInfo> zipInfos) {

        final List<Image> images = new ArrayList<>(zipInfos.size());
        for (int i = 0; i < zipInfos.size(); i++) {
            ZipInfo zipInfo = zipInfos.get(i);
            ZipAction.getInstance().zipImage(zipInfo);
        }
        return images;
    }

    @Nullable
    public Image zipImage(final ContentResolver cr, ZipInfo zipInfo) {
        String path = ZipAction.getInstance().zipImage(zipInfo);
        return ImageFind.getInstance().findImageByPath(cr, path);
    }

    private void postUi(final List<String> imageBeans, final ZipCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.onFinish(imageBeans);
            }
        });
    }


}
