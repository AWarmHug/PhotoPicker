package com.warm.library.find.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2017-11-13 09:49
 * 描述：
 */

public class BaseBean implements Parcelable {

    private String name;
    protected String id;
    protected long date;
    protected String path;
    protected Uri uri;

    public BaseBean(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public BaseBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BaseBean setName(String name) {
        this.name = name;
        return this;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public BaseBean setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public BaseBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeLong(this.date);
        dest.writeString(this.path);
        dest.writeParcelable(this.uri, flags);
    }

    protected BaseBean(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.date = in.readLong();
        this.path = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel source) {
            return new BaseBean(source);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };
}
