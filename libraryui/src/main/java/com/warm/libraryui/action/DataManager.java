package com.warm.libraryui.action;


import com.warm.libraryui.loader.ILoader;

/**
 * 作者：warm
 * 时间：2017-11-11 10:51
 * 描述：
 */

public class DataManager {

    private Config mConfig;

    private ILoader mILoader;

    private static DataManager mDataManager = new DataManager();

    private DataManager() {
    }

    public static DataManager getInstance() {
        return mDataManager;
    }

    public void init(ILoader loader){
        mILoader=loader;
    }

    public ILoader getILoader() {
        return mILoader;
    }

    public Config getConfig() {
        return mConfig;
    }

    public DataManager setConfig(Config mConfig) {
        this.mConfig = mConfig;
        return this;
    }
}
