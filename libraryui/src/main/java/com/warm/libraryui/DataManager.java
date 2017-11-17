package com.warm.libraryui;


import com.warm.libraryui.config.Config;
import com.warm.libraryui.loader.ImageLoader;

/**
 * 作者：warm
 * 时间：2017-11-11 10:51
 * 描述：
 */

public class DataManager {
    private Config config;

    private ImageLoader mImageLoader;

    private static DataManager mDataManager = new DataManager();

    private DataManager() {
    }

    public static DataManager getInstance() {
        return mDataManager;
    }

    public void init(ImageLoader loader){
        mImageLoader =loader;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


    public Config getConfig() {
        return config;
    }

    public DataManager setConfig(Config config) {
        this.config = config;
        return this;
    }
}
