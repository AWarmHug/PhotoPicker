package com.warm.pickerui.rx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.warm.picker.find.entity.Image;
import com.warm.pickerui.config.CropConfig;
import com.warm.pickerui.config.PickerConfig;
import com.warm.pickerui.ui.CropActivity;
import com.warm.pickerui.ui.PickerActivity;

import java.util.List;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

/**
 * 作者: 51hs_android
 * 时间: 2017/8/8
 * 简介:
 */

public class RxPhotoFragment extends Fragment {
    public static final int ALBUM = 1;
    public static final int CROP = 2;
    public static final String KEY_SELECT_IMAGES = "select_images";
    public static final String KEY_CROP_IMAGE_PATH ="crop_images";



    private static final String TAG = "RxPhotoFragment--";

    private int type;
    private Map<Integer, PublishSubject<Out>> mSubjectMap = new ArrayMap<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    void createSubject(int type) {
        this.type = type;
        this.mSubjectMap.put(type, PublishSubject.<Out>create());
    }


    void openAlbum(PickerConfig PickerConfig) {
        Intent intent = new Intent(getActivity(), PickerActivity.class);
        intent.putExtra(PickerActivity.KEY_PICKER_CONFIG, PickerConfig);
        startActivityForResult(intent, RxPhotoFragment.ALBUM);
    }

    void openCrop(CropConfig cropConfig) {
        Intent intent = new Intent(getActivity(), CropActivity.class);
        intent.putExtra(CropActivity.KEY_CROP_INFO, cropConfig);
        startActivityForResult(intent, RxPhotoFragment.CROP);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            Out out = new Out();
            PublishSubject<Out> subject = mSubjectMap.get(type);
            if (subject==null){
                return;
            }
            switch (requestCode) {
                case ALBUM:
                    List<Image> images = data.getParcelableArrayListExtra(KEY_SELECT_IMAGES);
                    out.setSelectImages(images);
                    if (images != null && images.size() != 0) {
                        subject.onNext(out);
                        subject.onComplete();
                    } else {
                        subject.onError(new Throwable("no select"));
                    }
                    break;
                case CROP:
                    String path = data.getStringExtra(KEY_CROP_IMAGE_PATH);
                    if (path!=null) {
                        out.setCropPath(path);
                        subject.onNext(out);
                        subject.onComplete();
                    } else {
                        subject.onError(new Throwable("no select"));
                    }
                    break;
                default:
                    subject.onError(new Throwable("no action"));
                    break;
            }
        }
    }

    public PublishSubject<Out> getSubjectByConfig(int type) {
        return mSubjectMap.get(type);
    }


    public class Out {
        private List<Image> selectImages;
        private String cropPath;

        public List<Image> getSelectImages() {
            return selectImages;
        }

        public Out setSelectImages(List<Image> selectImages) {
            this.selectImages = selectImages;
            return this;
        }

        public String getCropPath() {
            return cropPath;
        }

        public Out setCropPath(String cropPath) {
            this.cropPath = cropPath;
            return this;
        }
    }


}
