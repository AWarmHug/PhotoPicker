package com.warm.libraryui.config;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-11-10 16:14
 * 描述：
 */

public class PickerConfig implements Parcelable {



    /**
     * 选择几张照片
     */
    private int maxSelectNum =1;


    public int getMaxSelectNum() {
        return maxSelectNum;
    }

    public PickerConfig setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
        return this;
    }

    public PickerConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxSelectNum);
    }

    protected PickerConfig(Parcel in) {
        this.maxSelectNum = in.readInt();
    }

    public static final Creator<PickerConfig> CREATOR = new Creator<PickerConfig>() {
        @Override
        public PickerConfig createFromParcel(Parcel source) {
            return new PickerConfig(source);
        }

        @Override
        public PickerConfig[] newArray(int size) {
            return new PickerConfig[size];
        }
    };
}
