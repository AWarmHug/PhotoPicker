package com.warm.picker.pick.filter;

import android.os.Parcel;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：warm
 * 时间：2018-08-05 20:00
 * 描述：
 */
public class ImageFilter implements Filter {

    private int minSize = -1;
    private int maxSize = -1;
    private String[] without;

    public int getMinSize() {
        return minSize;
    }

    public ImageFilter setMinSize(int minSize) {
        this.minSize = minSize;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ImageFilter setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public String[] getWithout() {
        return without;
    }

    public ImageFilter setWithout(String... without) {
        this.without = without;
        return this;
    }
    public ImageFilter withOutGif() {
        return setWithout(MimeType.GIF);
    }

    @Override
    public FilterInfo filter() {
        StringBuilder sb = new StringBuilder();
        List<String> args = new ArrayList<>();

        if (minSize < 0) {
            minSize = 0;
        }
        sb.append(MediaStore.MediaColumns.SIZE).append(">?");
        args.add(String.valueOf(minSize));
        if (maxSize > 0 && maxSize > minSize) {
            sb.append(" AND ");
            sb.append(MediaStore.MediaColumns.SIZE).append("<?");
            args.add(String.valueOf(maxSize));
        }
        if (without != null) {
            for (String str : without) {
                sb.append(" AND ");
                sb.append(MediaStore.MediaColumns.MIME_TYPE).append("!=?");
                args.add(str);
            }
        }

        String selection = sb.toString();
        String[] selectionArgs = new String[args.size()];
        args.toArray(selectionArgs);

        return new FilterInfo(selection, selectionArgs);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.minSize);
        dest.writeInt(this.maxSize);
        dest.writeStringArray(this.without);
    }

    public ImageFilter() {
    }

    protected ImageFilter(Parcel in) {
        this.minSize = in.readInt();
        this.maxSize = in.readInt();
        this.without = in.createStringArray();
    }

    public static final Creator<ImageFilter> CREATOR = new Creator<ImageFilter>() {
        @Override
        public ImageFilter createFromParcel(Parcel source) {
            return new ImageFilter(source);
        }

        @Override
        public ImageFilter[] newArray(int size) {
            return new ImageFilter[size];
        }
    };
}
