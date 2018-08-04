package com.warm.demo;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.warm.pickerui.loader.ImageLoader;

/**
 * 作者：warm
 * 时间：2017-10-17 14:27
 * 描述：
 */

public class GlideLoader implements ImageLoader {


    @Override
    public void loadThumbnails(ImageView view, String path) {
        Glide.with(view.getContext())
                .asBitmap()
                .load(path)
                .into(view);
    }

    @Override
    public void load(ImageView iv, String path) {
        Glide.with(iv.getContext())
                .load(path)
                .into(iv);
    }
}
