package com.warm.library.zip;

import android.content.ContentResolver;

import com.warm.library.WorkExecutor;
import com.warm.library.find.bean.ImageBean;
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

    private static ImageZip zip = new ImageZip();

    public static ImageZip getInstance() {
        return zip;
    }


    public void zipImages(final ContentResolver cr, final List<ZipInfo> zipInfos, final ZipCallBack callBack) {
        final List<ImageBean> images = new ArrayList<>(zipInfos.size());
        for (int i = 0; i < zipInfos.size(); i++) {
            ZipInfo zipInfo = zipInfos.get(i);
            WorkExecutor.getInstance()
                    .runWorker(new ZipRunnable(cr, zipInfo, new ZipAction.ZipSingleCallBack() {
                        @Override
                        public void onFinish(ImageBean imageBean) {
                            images.add(imageBean);
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

    private void postUi(final List<ImageBean> imageBeans, final ZipCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.onFinish(imageBeans);
            }
        });
    }


}
