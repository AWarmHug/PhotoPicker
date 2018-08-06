package com.warm.picker.find.work;

import android.content.ContentResolver;

import com.warm.picker.find.filter.Filter;

import java.util.List;

/**
 * 作者：warm
 * 时间：2018-08-04 19:21
 * 描述：
 */
public interface MediaFinder<T> {

    List<T> findMedia(ContentResolver cr, String bucketId, Filter filter);

}
