package com.warm.picker.find.work;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.entity.Album;
import com.warm.picker.find.entity.Image;
import com.warm.picker.find.callback.AlbumFindCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2017-10-16 14:12
 * 描述：
 */
public class AlbumFind {

    private long mUnknowBucketId;
    private String mUnKnowBucketName = "unkonw";
    private static AlbumFind albumFind = new AlbumFind();

    private Map<String, Album> mMap;


    private AlbumFind() {
        this.mMap = new ArrayMap<>();
    }

    public static AlbumFind getInstance() {
        return albumFind;
    }


    public void findAlbum(final ContentResolver cr, final AlbumFindCallBack callBack) {
        WorkExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                findAlbum(cr);
                returnList(callBack);
            }
        });
    }


    private void returnList(AlbumFindCallBack callBack) {
        List<Album> list = new ArrayList<>();
        if (mMap != null) {
            //一般需要一个全部类
            int count = 0;
            for (Map.Entry<String, Album> entry : mMap.entrySet()) {
                list.add(entry.getValue());
                count += entry.getValue().getCount();
            }
            Album allBean = new Album();
            allBean.setBucketName(Album.BUCKET_NAME_ALL);
            allBean.setBucketId(Album.BUCKET_ID_ALL);
            allBean.setImage(list.get(0).getImage());
            allBean.setCount(count);
            list.add(0, allBean);
            mMap.clear();
        }
        postAlbum(list, callBack);
    }

    private void findAlbum(ContentResolver cr) {
        String[] projections = {MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = null;
        try {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, "0==0)" + " GROUP BY(" + MediaStore.Images.Media.BUCKET_ID, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                    String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
                    Album album = createAlbum(bucketId, bucketName);

                    findFirst(cr, bucketId, album);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void findFirst(ContentResolver cr, String bucketId, Album album) {
        String[] projections = Image.PROJECTIONS;
        String selection = MediaStore.Images.Media.BUCKET_ID + "=?";
        String[] selectionArgs = {bucketId};

        Cursor cursor = null;
        try {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " desc");

            if (cursor != null && cursor.moveToFirst()) {
                album.setCount(cursor.getCount());
                Image image = Image.createBy(cursor);
                album.setImage(image);
                if (album.getImage() != null) {
                    mMap.put(album.getBucketId(), album);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Album createAlbum(String bucketId, String bucketName) {
        Album album = null;
        if (!TextUtils.isEmpty(bucketId)) {
            album = mMap.get(bucketId);
        }
        if (album == null) {
            album = new Album();

            if (!TextUtils.isEmpty(bucketId)) {
                album.setBucketId(bucketId);
            } else {
                album.setBucketId(String.valueOf(mUnknowBucketId));
                mUnknowBucketId++;
            }

            if (!TextUtils.isEmpty(bucketName)) {
                album.setBucketName(bucketName);
            } else {
                album.setBucketName(mUnKnowBucketName);
                mUnknowBucketId++;
            }
            if (album.getImage() != null) {
                mMap.put(album.getBucketId(), album);
            }

        }

        return album;
    }


    private void postAlbum(final List<Album> images, final AlbumFindCallBack callBack) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                callBack.albumFind(images);
            }
        });
    }


}
