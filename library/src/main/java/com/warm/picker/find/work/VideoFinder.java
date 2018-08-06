package com.warm.picker.find.work;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.MediaFindCallBack;
import com.warm.picker.find.entity.Album;
import com.warm.picker.find.entity.Video;
import com.warm.picker.find.filter.Filter;
import com.warm.picker.find.filter.FilterInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2018-08-03 18:44
 * 描述：
 */
public class VideoFinder implements MediaFinder<Video> {


    private static VideoFinder sVideoFinder = new VideoFinder();

    private Map<Long, String> mThumbMap;

    private VideoFinder() {
    }

    public static VideoFinder getInstance() {
        return sVideoFinder;
    }

    @UiThread
    public void findVideo(final ContentResolver cr, final String bucketId, final Filter filter, final MediaFindCallBack<List<Video>> callBack) {
        WorkExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                List<Video> videos = findMedia(cr, bucketId, filter);
                postImages(videos, callBack);
            }
        });
    }

    private void postImages(final List<Video> images, final MediaFindCallBack<List<Video>> callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.mediaFound(images);
            }
        });
    }

    @Override
    public List<Video> findMedia(ContentResolver cr, String bucketId, Filter filter) {
        findThumb(cr);
        List<Video> videos = new ArrayList<>();
        String[] projections = Video.PROJECTIONS;

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
                selection = MediaStore.Video.VideoColumns.BUCKET_ID + "=" + bucketId + " AND " + filterStr;
            } else {
                selection = MediaStore.Video.VideoColumns.BUCKET_ID + "=" + bucketId;
            }
        }

        Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projections, selection, filterArgs, MediaStore.MediaColumns.DATE_ADDED + " desc");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Video video = Video.createBy(cursor);
                video.setThumbnailPath(mThumbMap.get(video.getId()));
                videos.add(video);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return videos;
    }


    @WorkerThread
    private void findThumb(ContentResolver cr) {

        if (mThumbMap == null || mThumbMap.size() == 0) {
            mThumbMap = new ArrayMap<>();
            String[] projections = {MediaStore.Video.Thumbnails.VIDEO_ID, MediaStore.MediaColumns.DATA};

            Cursor cursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projections, "kind = " + MediaStore.Video.Thumbnails.MINI_KIND, null, MediaStore.Video.Thumbnails.VIDEO_ID + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    mThumbMap.put(videoId, path);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }


}
