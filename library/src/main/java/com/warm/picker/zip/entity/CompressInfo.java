package com.warm.picker.zip.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-10-18 09:39
 * 描述：
 */

public class CompressInfo implements Parcelable {

    public String fromPath;
    public String toPath;
    public int width;
    public int height;
    public int maxSize;

    public CompressInfo(String fromPath, String toPath, int width, int height, int maxSize) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.width = width;
        this.height = height;
        this.maxSize = maxSize;
    }

    public CompressInfo(String fromPath, String toPath, int maxSize) {
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

    protected CompressInfo(Parcel in) {
        this.fromPath = in.readString();
        this.toPath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.maxSize = in.readInt();
    }

    public static final Creator<CompressInfo> CREATOR = new Creator<CompressInfo>() {
        @Override
        public CompressInfo createFromParcel(Parcel source) {
            return new CompressInfo(source);
        }

        @Override
        public CompressInfo[] newArray(int size) {
            return new CompressInfo[size];
        }
    };
}