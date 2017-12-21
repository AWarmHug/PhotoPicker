package com.warm.libraryui.config;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者：warm
 * 时间：2017-11-16 09:56
 * 描述：
 */

public class CropInfo implements Parcelable {

    public static final int CIRCLE = 1;
    public static final int SQUARE = 2;
    public static final int RECT = 3;


    private Uri imageUri;
    private String toPath;
    private int shape;
    private int showX = 1, showY = 1;
    private int outWidth, outHeight;


    public CropInfo(@SHAPE int shape, Uri imageUri, String toPath) {
        this.shape = shape;
        this.imageUri = imageUri;
        this.toPath = toPath;
    }

    public String getToPath() {
        return toPath;
    }

    public CropInfo setToPath(String toPath) {
        this.toPath = toPath;
        return this;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    @SHAPE
    public int getShape() {
        return shape;
    }

    public CropInfo setShape(@SHAPE int shape) {
        this.shape = shape;
        return this;
    }

    /**
     * 设置显示的宽高比，如：（1：2），只有是{@link SHAPE#RECT}时有效，否则都为1。
     *
     * @param showX 显示的宽度
     * @param showY 显示的高度
     * @return
     */
    public CropInfo setShow(int showX, int showY) {
        if (shape == RECT) {
            this.showX = showX;
            this.showY = showY;
        }
        return this;
    }

    public int[] getShow() {
        return new int[]{showX, showY};
    }

    /**
     * 输出图片的宽高，不是比例，就是实在的长度，请按照实际比例传入，否则图片会扭曲变形。
     *
     * @param outWidth
     * @param outHeight
     * @return
     */
    public CropInfo setOut(int outWidth, int outHeight) {
        this.outWidth = outWidth;
        this.outHeight = outHeight;
        return this;
    }

    public int[] getOut() {
        return new int[]{outWidth, outHeight};
    }

    @IntDef({CIRCLE, SQUARE, RECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SHAPE {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.imageUri, flags);
        dest.writeString(this.toPath);
        dest.writeInt(this.shape);
        dest.writeInt(this.showX);
        dest.writeInt(this.showY);
        dest.writeInt(this.outWidth);
        dest.writeInt(this.outHeight);
    }

    protected CropInfo(Parcel in) {
        this.imageUri = in.readParcelable(Uri.class.getClassLoader());
        this.toPath = in.readString();
        this.shape = in.readInt();
        this.showX = in.readInt();
        this.showY = in.readInt();
        this.outWidth = in.readInt();
        this.outHeight = in.readInt();
    }

    public static final Creator<CropInfo> CREATOR = new Creator<CropInfo>() {
        @Override
        public CropInfo createFromParcel(Parcel source) {
            return new CropInfo(source);
        }

        @Override
        public CropInfo[] newArray(int size) {
            return new CropInfo[size];
        }
    };
}
