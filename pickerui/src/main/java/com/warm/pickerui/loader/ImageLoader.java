package com.warm.pickerui.loader;

import android.widget.ImageView;

/**
 * 作者：warm
 * 时间：2017-10-17 14:20
 * 描述：
 */

public interface ImageLoader {

    void loadThumbnails(ImageView view, String path);

    void load(ImageView iv, String path);

}
