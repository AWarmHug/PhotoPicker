package com.warm.library.find.work;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;

import com.warm.library.WorkExecutor;
import com.warm.library.find.bean.AlbumBean;
import com.warm.library.find.bean.ImageBean;
import com.warm.library.find.callback.ImageFindCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2017-10-17 10:22
 * 描述：
 */

public class ImageFind {

    private static ImageFind imageFind = new ImageFind();

    private Map<String, String> mThumbMap;

    private ImageFind() {
    }

    public static ImageFind getInstance() {
        return imageFind;
    }


    @UiThread
    public void findImage(final ContentResolver cr, final String backetId, final ImageFindCallBack callBack) {
        WorkExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                List<ImageBean> images = findImagesByBacketId(cr, backetId);
                postImages(images, callBack);

            }
        });

    }

    @WorkerThread
    public List<ImageBean> findImagesByBacketId(ContentResolver cr, String backetId) {
        findThumb(cr);

        List<ImageBean> images = new ArrayList<>();

        String projections[] = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};
        String selection = MediaStore.Images.Media.BUCKET_ID + "=?";
        String[] selectionArgs = {backetId};

        Cursor cursor = null;
        try {
            if (backetId.equals(AlbumBean.BUCKET_ID_ALL)) {
                cursor = MediaStore.Images.Media.query(cr, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");

            } else {
                cursor = MediaStore.Images.Media.query(cr, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " desc");
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                    int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                    ImageBean image = new ImageBean(imageId, path);
                    image.setName(name);
                    image.setDate(date);
                    image.setWidth(width);
                    image.setHeight(height);
                    image.setThumbnailPath(mThumbMap.get(imageId));
//                    if (!images.contains(image)) {
                    images.add(image);
//                    }
                    image.setUri(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return images;
    }

    @WorkerThread
    private void findThumb(ContentResolver cr) {

        if (mThumbMap == null || mThumbMap.size() == 0) {
            mThumbMap = new ArrayMap<>();
            String projections[] = {MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};

            Cursor cursor = null;
            try {
                cursor = MediaStore.Images.Thumbnails.queryMiniThumbnails(cr, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, MediaStore.Images.Thumbnails.MINI_KIND, projections);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                        mThumbMap.put(imageId, path);
                    } while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 根据path，获取图片信息
     *
     * @param cr
     * @param outPath
     * @return
     */
    @WorkerThread
    public ImageBean findImageByPath(ContentResolver cr, String outPath) {
        findThumb(cr);

        ImageBean image = new ImageBean();

        String projections[] = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {outPath};

        Cursor cursor = null;
        try {

            cursor = MediaStore.Images.Media.query(cr, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                image.setId(imageId);
                image.setName(name);
                image.setPath(path);
                image.setDate(date);
                image.setWidth(width);
                image.setHeight(height);
                image.setThumbnailPath(mThumbMap.get(imageId));
                image.setUri(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return image;
    }

    /**
     * 根据path，获取图片信息
     *
     * @param cr
     * @param uri
     * @return
     */
    @WorkerThread
    public ImageBean findImageByUri(ContentResolver cr, Uri uri) {
        findThumb(cr);

        ImageBean image = new ImageBean();

        String projections[] = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};


        Cursor cursor = null;
        try {

            cursor = MediaStore.Images.Media.query(cr, uri, projections, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                image.setId(imageId);
                image.setName(name);
                image.setPath(path);
                image.setDate(date);
                image.setWidth(width);
                image.setHeight(height);
                image.setThumbnailPath(mThumbMap.get(imageId));
                image.setUri(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return image;
    }

    private void postImages(final List<ImageBean> images, final ImageFindCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.imageFind(images);
            }
        });
    }


}
