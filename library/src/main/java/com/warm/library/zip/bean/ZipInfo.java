package com.warm.library.zip.bean;

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
    public int size;

    public ZipInfo(String fromPath, String toPath, int width, int height, int size) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public ZipInfo(String fromPath, String toPath, int size) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.size = size;
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
        dest.writeInt(this.size);
    }

    protected ZipInfo(Parcel in) {
        this.fromPath = in.readString();
        this.toPath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.size = in.readInt();
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