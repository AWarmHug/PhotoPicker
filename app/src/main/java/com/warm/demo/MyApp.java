package com.warm.demo;

import android.app.Application;
import android.os.Environment;

import com.warm.pickerui.config.PickerUI;

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
        // 不要使用data/data下的目录 如getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // ，可能导致拍照后再次扫描时，扫描不到的问题 在乐视2 Android6.0版本上会存在，虽然拍照后仍然会添加到，但是是我new出来的。

        PickerUI.getInstance()
                .setCameraDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Camera")
                .setCameraIcon(R.drawable.ic_vec_take_photo)
                .init(new GlideLoader());
    }
}
