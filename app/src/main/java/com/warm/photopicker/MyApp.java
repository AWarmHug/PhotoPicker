package com.warm.photopicker;

import android.app.Application;
import android.os.Environment;

import com.warm.libraryui.config.DataManager;
import com.warm.libraryui.config.Config;

import java.io.File;


/**
 * 作者：warm
 * 时间：2017-11-11 10:54
 * 描述：
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Config config=new Config(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "Camera")
                .setCameraIcon(R.drawable.ic_vec_take_photo)
                .setSelectIcon(R.drawable.ic_vec_selected,R.drawable.ic_vec_unselected);

        DataManager.getInstance()
                .setConfig(config)
                .init(new GlideLoader());
    }
}
