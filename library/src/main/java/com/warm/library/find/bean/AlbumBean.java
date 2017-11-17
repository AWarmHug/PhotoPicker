package com.warm.library.find.bean;

/**
 * 作者：warm
 * 时间：2017-10-16 16:58
 * 描述：
 */

public class AlbumBean {

    public static final String BUCKET_ID_ALL = "all";
    public static final String BUCKET_NAME_ALL = "全部图片";


    private int count;
    private String bucketId;
    private String bucketName;
    private ImageBean image;
    private boolean selected;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "mCount=" + count +
                ", mBucketId='" + bucketId + '\'' +
                ", mBucketName='" + bucketName + '\'' +
                ", mImage=" + image +
                ", mSelected=" + selected +
                '}';
    }
}
