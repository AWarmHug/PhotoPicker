package com.warm.library.zip;

import android.content.ContentResolver;

import com.warm.library.WorkExecutor;
import com.warm.library.find.bean.ImageBean;
import com.warm.library.find.work.ImageFind;
import com.warm.library.zip.bean.ZipInfo;

import java.util.ArrayList;
import java.util.List;

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


    public void zipImages(final ContentResolver cr, final List<ZipInfo> zipInfos, final ZipCallBack callBack) {
        final List<String> images = new ArrayList<>(zipInfos.size());
        for (int i = 0; i < zipInfos.size(); i++) {
            ZipInfo zipInfo = zipInfos.get(i);
            WorkExecutor.getInstance()
                    .runWorker(new ZipRunnable(cr, zipInfo, new ZipAction.ZipSingleCallBack() {
                        @Override
                        public void onFinish(String path) {
                            images.add(path);
                            if (images.size() == zipInfos.size()) {
                                postUi(images, callBack);
                            }
                        }
                    }));
        }
    }

    public List<ImageBean> zipImage(final ContentResolver cr, final List<ZipInfo> zipInfos) {

        final List<ImageBean> images = new ArrayList<>(zipInfos.size());
        for (int i = 0; i < zipInfos.size(); i++) {
            ZipInfo zipInfo = zipInfos.get(i);
            ZipAction.getInstance().zipImage(cr, zipInfo);
        }
        return images;
    }

    public ImageBean zipImage(final ContentResolver cr, ZipInfo zipInfo) {
        String path = ZipAction.getInstance().zipImage(cr, zipInfo);

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
