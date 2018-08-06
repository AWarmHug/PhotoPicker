package com.warm.picker.find.entity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

/**
 * 作者：warm
 * 时间：2017-10-16 14:17
 * 描述：
 */

public class Image extends BaseMedia {

    public static final String[] PROJECTIONS = {MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.MIME_TYPE,
            BUCKET_ID,
            BUCKET_DISPLAY_NAME,
            DATE_TAKEN,
            MediaStore.Images.ImageColumns.DESCRIPTION,
            MediaStore.Images.ImageColumns.ORIENTATION};

    public static Image createBy(Cursor cursor) {
        Image image = new Image();
        image.setPropertyBy(cursor);
        image.setDescription(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION)));
        image.setOrientation(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)));
        return image;
    }

    public Image() {
    }

    public Image(long id, String data) {
        super(id, data);
    }

    private String description;


    private int orientation;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }


    public Uri getUri() {
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.description);
        dest.writeInt(this.orientation);
    }

    protected Image(Parcel in) {
        super(in);
        this.description = in.readString();
        this.orientation = in.readInt();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}

