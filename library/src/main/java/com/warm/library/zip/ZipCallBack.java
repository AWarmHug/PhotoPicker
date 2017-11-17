package com.warm.library.zip;


import com.warm.library.find.bean.ImageBean;

import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-18 09:38
 * 描述：
 */

public interface ZipCallBack {
    void onFinish(List<ImageBean> imageBean);
}
