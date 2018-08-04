package com.warm.pickerui.ui.adapter;

/**
 * 作者：warm
 * 时间：2017-11-10 17:14
 * 描述：
 */

public interface OnItemSelectListener<T> {

    void cameraClick();

    void itemClick(int position, T t);

    void itemSelect(int position, T imageBean);
}
