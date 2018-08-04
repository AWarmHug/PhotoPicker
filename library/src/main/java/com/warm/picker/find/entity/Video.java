package com.warm.picker.find.entity;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 作者：warm
 * 时间：2018-08-03 16:32
 * 描述：
 */
public class Video extends BaseMedia {


    public static Video createBy(Cursor cursor) {
        Video video = new Video();
        video.setPropertyBy(cursor);
        video.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)));
        video.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST)));
        video.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM)));
        video.setResolution(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION)));
        return video;
    }

    /**
     * 时长
     */
    private long duration;

    /**
     * 作者
     */
    private String artist;

    /**
     * 视频相册
     */
    private String album;

    /**
     * 分辨率 XxY
     */
    private String resolution;


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Uri getUri() {
        return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
    }
}
