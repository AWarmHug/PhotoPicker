package com.warm.picker.pick.filter;

/**
 * 作者：warm
 * 时间：2018-08-06 09:24
 * 描述：
 */
public class FilterInfo {
    String selection;
    String[] selectionArgs;

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public FilterInfo(String selection, String[] selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }
}
