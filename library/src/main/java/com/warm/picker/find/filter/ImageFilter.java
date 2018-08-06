package com.warm.picker.find.filter;

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


}
