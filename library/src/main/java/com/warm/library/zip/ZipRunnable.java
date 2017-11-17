package com.warm.library.zip;

import android.content.ContentResolver;

import com.warm.library.find.bean.ImageBean;
import com.warm.library.find.work.ImageFind;
import com.warm.library.zip.bean.ZipInfo;


/**
 * 作者：warm
 * 时间：2017-11-15 16:02
 * 描述：
 */

public class ZipRunnable implements Runnable {

    private ContentResolver cr;
    private ZipInfo zipInfo;
    private ZipAction.ZipSingleCallBack callBack;


    public ZipRunnable(ContentResolver cr, ZipInfo zipInfo, ZipAction.ZipSingleCallBack callBack) {
        this.cr = cr;
        this.zipInfo = zipInfo;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        String path = ZipAction.getInstance().zipImage(cr, zipInfo);
        ImageBean imageBean = ImageFind.getInstance().findImageByPath(cr, path);
        callBack.onFinish(imageBean);

    }



}
