package com.warm.picker.compress;

import com.warm.picker.WorkExecutor;
import com.warm.picker.compress.entity.CompressInfo;
import com.warm.picker.compress.work.Compressor;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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


    public void zipImages(final Compressor compressor, final List<CompressInfo> compressInfos, final CompressCallBack callBack) {
        final List<String> images = new Vector<>(compressInfos.size());
        for (int i = 0; i < compressInfos.size(); i++) {
            final CompressInfo compressInfo = compressInfos.get(i);

            WorkExecutor.getInstance().runWorker(new Runnable() {
                @Override
                public void run() {
                    String path = compressor.compress(compressInfo);
                    images.add(path);
                    if (images.size() == compressInfos.size()) {
                        postUi(images, callBack);
                    }
                }
            });
        }
    }


    public List<String> zipImages(final Compressor compressor, final List<CompressInfo> compressInfos) {
        final List<String> images = new Vector<>(compressInfos.size());
        for (int i = 0; i < compressInfos.size(); i++) {
            final CompressInfo compressInfo = compressInfos.get(i);
            try {
                images.add(WorkExecutor.getInstance().getExecutorService().submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return compressor.compress(compressInfo);
                    }
                }).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return images;
    }


    private void postUi(final List<String> imageBeans, final CompressCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.onCompress(imageBeans);
            }
        });
    }


}
