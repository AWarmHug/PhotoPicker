package com.warm.library.find.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * 作者：warm
 * 时间：2017-10-16 14:17
 * 描述：
 */

public class ImageBean extends BaseBean implements Parcelable {

    private boolean selected;
    private int width;
    private int height;
    private String thumbnailPath;


    public int getWidth() {
        return width;
    }

    public ImageBean setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public ImageBean setHeight(int height) {
        this.height = height;
        return this;
    }

    public ImageBean(String id, String path) {
        super(id, path);
    }

    public String getThumbnailPath() {
        return isFileValid(thumbnailPath) ? thumbnailPath : path;
    }

    public static boolean isFileValid(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return isFileValid(file);
    }

    public static boolean isFileValid(File file) {
        return file.exists() && file.isFile() && file.length() > 0 && file.canRead();
    }


    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            // 引用相等
            return true;
        } else if (obj == null) {
            // 对象为null
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            //判断类型，// 域属性相等
            ImageBean other = (ImageBean) obj;

            return other.path.equals(path) && !(TextUtils.isEmpty(other.getPath()) || TextUtils.isEmpty(path));
        }
    }


    @Override
    public String toString() {
        return "ImageBean{" +
                "seleced=" + selected +
                ", id=" + id +
                ", date=" + date +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public ImageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.thumbnailPath);
    }

    protected ImageBean(Parcel in) {
        super(in);
        this.selected = in.readByte() != 0;
        this.width = in.readInt();
        this.height = in.readInt();
        this.thumbnailPath = in.readString();
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
