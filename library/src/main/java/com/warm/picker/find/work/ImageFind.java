package com.warm.picker.find.work;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.callback.ImageFindCallBack;
import com.warm.picker.find.entity.Album;
import com.warm.picker.find.entity.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2017-10-17 10:22
 * 描述：
 */

public class ImageFind {

    private static ImageFind sImageFind = new ImageFind();

    private Map<Long, String> mThumbMap;

    private ImageFind() {
    }

    public static ImageFind getInstance() {
        return sImageFind;
    }


    @UiThread
    public void findImage(final ContentResolver cr, final String backetId, final ImageFindCallBack callBack) {
        WorkExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                List<Image> images = findImagesByBacketId(cr, backetId);
                postImages(images, callBack);

            }
        });

    }

    @WorkerThread
    public List<Image> findImagesByBacketId(ContentResolver cr, String backetId) {
        findThumb(cr);

        List<Image> images = new ArrayList<>();

        String[] projections = Image.PROJECTIONS;
//        String[] projections = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};

        String selection = MediaStore.Images.Media.BUCKET_ID + "= ?";
        String[] selectionArgs = {backetId};

        long current = System.currentTimeMillis();

        Cursor cursor = null;
        try {
            if (backetId.equals(Album.BUCKET_ID_ALL)) {
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");
            } else {
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " desc");
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Image image = Image.createBy(cursor);
                    image.setThumbnailPath(mThumbMap.get(image.getId()));
                    if (!images.contains(image)) {
                    images.add(image);
                    }
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d("ssss", "findImagesByBacketId: " + (System.currentTimeMillis() - current));
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
                        long imageId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
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
    @Nullable
    @WorkerThread
    public Image findImageByPath(ContentResolver cr, String outPath) {
        findThumb(cr);

        Image image = null;

        String[] projections = Image.PROJECTIONS/*{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT}*/;
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {outPath};

        Cursor cursor = null;
        try {
            cursor = MediaStore.Images.Media.query(cr, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                image = Image.createBy(cursor);
                image.setThumbnailPath(mThumbMap.get(image.getId()));
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
    public Image findImageByUri(ContentResolver cr, Uri uri) {
        findThumb(cr);

        Image image = null;

        String[] projections = Image.PROJECTIONS/*{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT}*/;


        Cursor cursor = null;
        try {

            cursor = MediaStore.Images.Media.query(cr, uri, projections, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                image = Image.createBy(cursor);
                image.setThumbnailPath(mThumbMap.get(image.getId()));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return image;
    }

    private void postImages(final List<Image> images, final ImageFindCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.imageFind(images);
            }
        });
    }


}
