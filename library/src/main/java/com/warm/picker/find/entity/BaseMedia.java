package com.warm.picker.find.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * 作者：warm
 * 时间：2017-11-13 09:49
 * 描述：
 */

public class BaseMedia implements Parcelable {


    /**
     * The bucket id of the image. This is a read-only property that
     * is automatically computed from the DATA column.
     * <P>Type: TEXT</P>
     */
    public static final String BUCKET_ID = "bucket_id";

    /**
     * The bucket display name of the image. This is a read-only property that
     * is automatically computed from the DATA column.
     * <P>Type: TEXT</P>
     */
    public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";

    /**
     * The date & time that the image was taken in units
     * of milliseconds since jan 1, 1970.
     * <P>Type: INTEGER</P>
     */
    public static final String DATE_TAKEN = "datetaken";

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    protected long id;
    protected String name;
    protected String data;
    protected long dateAdd;
    protected long size;
    protected int width;
    protected int height;
    protected String bucketId;
    protected String bucketDisplayName;
    protected long dateTaken;

    protected String thumbnailPath;


    public void setPropertyBy(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
        data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        dateAdd = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
        size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
        width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH));
        height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
        bucketId = cursor.getString(cursor.getColumnIndex(BUCKET_ID));
        bucketDisplayName = cursor.getString(cursor.getColumnIndex(BUCKET_DISPLAY_NAME));
        dateTaken = cursor.getLong(cursor.getColumnIndex(DATE_TAKEN));
    }

    public BaseMedia() {
    }

    public BaseMedia(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(long dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketDisplayName() {
        return bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName) {
        this.bucketDisplayName = bucketDisplayName;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }



    public String getThumbnailPath() {
        return isFileValid(thumbnailPath) ? thumbnailPath : data;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseMedia media = (BaseMedia) o;

        if (id != media.id) return false;
        return data != null ? data.equals(media.data) : media.data == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.data);
        dest.writeLong(this.dateAdd);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketDisplayName);
        dest.writeLong(this.dateTaken);
        dest.writeString(this.thumbnailPath);
    }

    protected BaseMedia(Parcel in) {
        this.selected = in.readByte() != 0;
        this.id = in.readLong();
        this.name = in.readString();
        this.data = in.readString();
        this.dateAdd = in.readLong();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.bucketId = in.readString();
        this.bucketDisplayName = in.readString();
        this.dateTaken = in.readLong();
        this.thumbnailPath = in.readString();
    }

    public static final Creator<BaseMedia> CREATOR = new Creator<BaseMedia>() {
        @Override
        public BaseMedia createFromParcel(Parcel source) {
            return new BaseMedia(source);
        }

        @Override
        public BaseMedia[] newArray(int size) {
            return new BaseMedia[size];
        }
    };
}
