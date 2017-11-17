package com.warm.photopicker;

import android.app.Application;

import com.warm.libraryui.action.DataManager;


/**
 * 作者：warm
 * 时间：2017-11-11 10:54
 * 描述：
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.getInstance().init(new GlideLoader());
    }
}
