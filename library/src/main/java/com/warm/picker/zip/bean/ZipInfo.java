package com.warm.picker.zip.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-10-18 09:39
 * 描述：
 */

public class ZipInfo implements Parcelable {

    public String fromPath;
    public String toPath;
    public int width;
    public int height;
    public int maxSize;

    public ZipInfo(String fromPath, String toPath, int width, int height, int maxSize) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.width = width;
        this.height = height;
        this.maxSize = maxSize;
    }

    public ZipInfo(String fromPath, String toPath, int maxSize) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.maxSize = maxSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fromPath);
        dest.writeString(this.toPath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.maxSize);
    }

    protected ZipInfo(Parcel in) {
        this.fromPath = in.readString();
        this.toPath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.maxSize = in.readInt();
    }

    public static final Creator<ZipInfo> CREATOR = new Creator<ZipInfo>() {
        @Override
        public ZipInfo createFromParcel(Parcel source) {
            return new ZipInfo(source);
        }

        @Override
        public ZipInfo[] newArray(int size) {
            return new ZipInfo[size];
        }
    };
}