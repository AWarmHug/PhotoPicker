package com.warm.picker.find.work;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.MediaFindCallBack;
import com.warm.picker.find.entity.Album;
import com.warm.picker.find.entity.Image;
import com.warm.picker.find.filter.Filter;
import com.warm.picker.find.filter.FilterInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2017-10-17 10:22
 * 描述：
 */

public class ImageFinder implements MediaFinder<Image> {

    private static ImageFinder sImageFinder = new ImageFinder();

    private Map<Long, String> mThumbMap;

    private ImageFinder() {
    }

    public static ImageFinder getInstance() {
        return sImageFinder;
    }




    @UiThread
    public void findImage(final ContentResolver cr, final String bucketId, final Filter filter, final MediaFindCallBack<List<Image>> callBack) {
        WorkExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                List<Image> images = findMedia(cr, bucketId, filter);
                postImages(images, callBack);

            }
        });
    }

    //需要设置args 斜杠不能直接加
    private static final String CONJUNCTION_SQL = "=? or";
    private static final String SELECTION_IMAGE_MIME_TYPE = Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + "=?";
    private static final String SELECTION_ID = Images.Media.BUCKET_ID + "=? and (" + SELECTION_IMAGE_MIME_TYPE + " )";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPG = "image/jpg";
    private static final String IMAGE_GIF = "image/gif";

    @WorkerThread
    public List<Image> findMedia(ContentResolver cr, String bucketId, Filter filter) {
        findThumb(cr);

        List<Image> images = new ArrayList<>();

        String[] projections = Image.PROJECTIONS;

        long current = System.currentTimeMillis();

        String selection = null;
        String filterStr = null;
        String[] filterArgs = null;
        FilterInfo filterInfo = null;
        if (filter != null && filter.filter() != null) {
            filterInfo = filter.filter();
        }
        if (filterInfo != null) {
            filterStr = filterInfo.getSelection();
            filterArgs = filterInfo.getSelectionArgs();
        }

        if (bucketId.equals(Album.BUCKET_ID_ALL)) {
            if (!TextUtils.isEmpty(filterStr)) {
                selection = filterStr;
            }
        } else {
            if (!TextUtils.isEmpty(filterStr)) {
                selection = MediaStore.Images.ImageColumns.BUCKET_ID + "=" + bucketId + " AND " + filterStr;
            } else {
                selection = MediaStore.Images.ImageColumns.BUCKET_ID + "=" + bucketId;
            }
        }

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, filterArgs, MediaStore.MediaColumns.DATE_ADDED + " desc");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Image image = Image.createBy(cursor);
                image.setThumbnailPath(mThumbMap.get(image.getId()));
                images.add(image);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        Log.d("ssss", "findMedia: " + (System.currentTimeMillis() - current));
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


    /**
     * 将压缩的图片信息添加到ContentProvider中，在后面会根据路径查找图片时会使用到
     *
     * @param cr
     * @param file
     * @return
     */
    public Uri saveContentProvider(ContentResolver cr, File file, BitmapFactory.Options options) {
        return saveContentProvider(cr, file, options, System.currentTimeMillis());
    }

    /**
     * 将压缩的图片信息添加到ContentProvider中，在后面会根据路径查找图片时会使用到
     *
     * @param cr
     * @param file
     * @return
     */
    @WorkerThread
    public Uri saveContentProvider(ContentResolver cr, File file, BitmapFactory.Options options, long time) {

        //判断是否该路径是否存在Uri，true：update；false：insert
        Image image = ImageFinder.getInstance().findImageByPath(cr, file.getPath());
        if (image != null && image.getId() != 0) {
            return image.getUri();
        } else {

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            long dateSeconds = time / 1000;

            // Save the screenshot to the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.ImageColumns.DATA, file.getPath());
            values.put(MediaStore.Images.ImageColumns.TITLE, file.getName());
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION, file.getName());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getParentFile().getName());
            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, time);
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, options.outMimeType);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                values.put(MediaStore.Images.ImageColumns.WIDTH, options.outWidth);
                values.put(MediaStore.Images.ImageColumns.HEIGHT, options.outHeight);
            }
            values.put(MediaStore.Images.ImageColumns.SIZE, file.length());
            return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    private void postImages(final List<Image> images, final MediaFindCallBack<List<Image>> callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.mediaFound(images);
            }
        });
    }


}
