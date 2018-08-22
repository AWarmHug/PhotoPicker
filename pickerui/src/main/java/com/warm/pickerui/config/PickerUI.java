package com.warm.pickerui.config;


import com.warm.pickerui.loader.ImageLoader;

/**
 * 作者：warm
 * 时间：2017-11-11 10:51
 * 描述：
 */

public class PickerUI {
    private Config config;

    private ImageLoader mImageLoader;

    private static PickerUI sMPickerUI = new PickerUI();

    private PickerUI() {
    }

    public static PickerUI getInstance() {
        return sMPickerUI;
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

    public PickerUI setConfig(Config config) {
        this.config = config;
        return this;
    }
}
