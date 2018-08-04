package com.warm.picker.find.entity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：warm
 * 时间：2018-08-03 16:05
 * 描述：
 */
public class Thumbnail implements Parcelable {

    /**
     * Path to the thumbnail file on disk.
     * <p>
     * Note that apps may not have filesystem permissions to directly
     * access this path. Instead of trying to open this path directly,
     * apps should use
     * {@link ContentResolver#openFileDescriptor(Uri, String)} to gain
     * access.
     * <p>
     * Type: TEXT
     */
    public static final String DATA = "_data";

    /**
     * The original image for the thumbnal
     * <P>Type: INTEGER (ID from Images table)</P>
     */
    public static final String IMAGE_ID = "image_id";

    /**
     * The original image for the thumbnal
     * <P>Type: INTEGER (ID from Video table)</P>
     */
    public static final String VIDEO_ID = "video_id";


    /**
     * The kind of the thumbnail
     * <P>Type: INTEGER (One of the values below)</P>
     */
    public static final String KIND = "kind";

    public static final int MINI_KIND = 1;
    public static final int FULL_SCREEN_KIND = 2;
    public static final int MICRO_KIND = 3;

    /**
     * The width of the thumbnal
     * <P>Type: INTEGER (long)</P>
     */
    public static final String WIDTH = "width";

    /**
     * The height of the thumbnail
     * <P>Type: INTEGER (long)</P>
     */
    public static final String HEIGHT = "height";


    public static Thumbnail createBy(String type_id, Cursor cursor) {
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.id = cursor.getString(cursor.getColumnIndex(type_id));
        thumbnail.data = cursor.getString(cursor.getColumnIndex(DATA));
        thumbnail.width = cursor.getLong(cursor.getColumnIndex(WIDTH));
        thumbnail.height = cursor.getLong(cursor.getColumnIndex(HEIGHT));
        return thumbnail;
    }


    /**
     * Image/Video Id
     */
    private String id;

    /**
     * path
     */
    private String data;

    private long width;

    private long height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.data);
        dest.writeLong(this.width);
        dest.writeLong(this.height);
    }

    public Thumbnail() {
    }

    protected Thumbnail(Parcel in) {
        this.id = in.readString();
        this.data = in.readString();
        this.width = in.readLong();
        this.height = in.readLong();
    }

    public static final Creator<Thumbnail> CREATOR = new Creator<Thumbnail>() {
        @Override
        public Thumbnail createFromParcel(Parcel source) {
            return new Thumbnail(source);
        }

        @Override
        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }
    };
}
