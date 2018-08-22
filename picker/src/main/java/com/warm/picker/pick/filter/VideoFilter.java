package com.warm.picker.pick.filter;

import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：warm
 * 时间：2018-08-05 19:37
 * 描述：
 */
public class VideoFilter extends ImageFilter {
    private int minDuration = -1;
    private int maxDuration = -1;

    public int getMinDuration() {
        return minDuration;
    }

    public VideoFilter setMinDuration(int minDuration) {
        this.minDuration = minDuration;
        return this;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public VideoFilter setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }

    @Override
    public FilterInfo filter() {
        FilterInfo info = super.filter();

        StringBuilder sb = new StringBuilder();
        sb.append(info.selection);
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList(info.selectionArgs));

        if (minDuration < 0) {
            minDuration = 0;
        }
        sb.append(MediaStore.Video.VideoColumns.DURATION).append(">?");
        args.add(String.valueOf(minDuration));
        if (maxDuration > 0) {
            sb.append(" AND ");
            sb.append(MediaStore.Video.VideoColumns.DURATION).append(" <? ");
            args.add(String.valueOf(maxDuration));
        }
        info.selection=sb.toString();
        info.selectionArgs= (String[]) args.toArray();
        return info;
    }
}
