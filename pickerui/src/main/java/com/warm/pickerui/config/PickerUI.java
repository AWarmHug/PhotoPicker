package com.warm.pickerui.config;


import com.warm.pickerui.loader.ImageLoader;

/**
 * 作者：warm
 * 时间：2017-11-11 10:51
 * 描述：
 */

public class PickerUI {
    /**
     * 调用系统相机拍照，存放的图片文件夹，图片文件名为IMG_time.jpg
     */
    private String cameraDir;

    /**
     * 选择界面中相机按钮的图标
     */
    private int cameraIcon;

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

    public String getCameraDir() {
        return cameraDir;
    }

    public PickerUI setCameraDir(String cameraDir) {
        this.cameraDir = cameraDir;
        return this;
    }

    public int getCameraIcon() {
        return cameraIcon;
    }

    public PickerUI setCameraIcon(int cameraIcon) {
        this.cameraIcon = cameraIcon;
        return this;
    }
}
