package com.warm.picker.find.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-10-16 16:58
 * 描述：
 */

public class Album implements Parcelable {

    public static final String BUCKET_ID_ALL = "all";
    public static final String BUCKET_NAME_ALL = "全部图片";


    private int count;
    private String bucketId;
    private String bucketName;
    private Image image;
    private boolean selected;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketName);
        dest.writeParcelable(this.image, flags);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.count = in.readInt();
        this.bucketId = in.readString();
        this.bucketName = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
