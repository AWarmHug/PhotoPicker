package com.warm.libraryui.config;

import android.support.annotation.DrawableRes;

/**
 * 作者：warm
 * 时间：2017-11-17 10:44
 * 描述：
 */

public class Config {
    /**
     * 调用系统相机拍照，存放的图片文件夹，图片文件名为IMG_time.jpg
     */
    private String cameraDir;

    /**
     * 选择界面中相机按钮的图标
     */
    private int cameraIcon;

    /**
     * 图片选择界面右上角的选择按钮，选中和未选中
     */
    private int selectIcon;


    public String getCameraDir() {
        return cameraDir;
    }

    public Config setSelectIcon(@DrawableRes int drawable) {
        selectIcon = drawable;
        return this;
    }

    public int getSelectIcon() {
        return selectIcon;
    }

    public int getCameraIcon() {
        return cameraIcon;
    }

    public Config setCameraIcon(int cameraIcon) {
        this.cameraIcon = cameraIcon;
        return this;
    }

    /**
     * @param cameraDir 尽量不要设置data/data下的目录，可能会导致拍照后再次扫描时，查询不到。
     *                  目前发现在乐视手机 6.0版本上存在这一的问题。
     */
    public Config(String cameraDir) {
        this.cameraDir = cameraDir;
    }
}
