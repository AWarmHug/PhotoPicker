package com.warm.libraryui.action;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-11-10 16:14
 * 描述：
 */

public class Config implements Parcelable {



    /**
     * 选择几张照片
     */
    private int maxSelectNum =1;

    /**
     * 调用系统相机拍照，存放的图片文件夹，图片文件名为IMG_time.jpg
     */
    private String cameraDir;


    public int getMaxSelectNum() {
        return maxSelectNum;
    }

    public Config setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
        return this;
    }

    public String getCameraDir() {
        return cameraDir;
    }

    public Config setCameraDir(String cameraDir) {
        this.cameraDir = cameraDir;
        return this;
    }

    public Config() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxSelectNum);
    }

    protected Config(Parcel in) {
        this.maxSelectNum = in.readInt();
    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel source) {
            return new Config(source);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };
}
