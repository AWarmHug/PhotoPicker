package com.warm.pickerui.rx;

import android.support.v7.app.AppCompatActivity;

import com.warm.picker.pick.entity.Image;
import com.warm.picker.compress.CompressCallBack;
import com.warm.picker.compress.ImageZip;
import com.warm.picker.compress.entity.CompressInfo;
import com.warm.picker.compress.work.ImageCompressor;
import com.warm.pickerui.config.CropConfig;
import com.warm.pickerui.config.PickerConfig;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 作者: 51hs_android
 * 时间: 2017/8/8
 * 简介:
 */

public class RxPhoto {


    private RxPhotoFragment rxPhotoFragment;

    public RxPhoto(AppCompatActivity activity) {
        rxPhotoFragment = getRxFragment(activity);
    }

    private RxPhotoFragment getRxFragment(AppCompatActivity activity) {
        //先寻找是否存在Fragment，如果为空就新建。
        RxPhotoFragment rxPhotoFragment = findRxPhotoFragment(activity);
        if (rxPhotoFragment == null) {
            rxPhotoFragment = new RxPhotoFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(rxPhotoFragment, RxPhotoFragment.class.getSimpleName())
                    .commitNow();
        }
        return rxPhotoFragment;
    }

    private RxPhotoFragment findRxPhotoFragment(AppCompatActivity activity) {
        return (RxPhotoFragment) activity.getSupportFragmentManager().findFragmentByTag(RxPhotoFragment.class.getSimpleName());
    }


    public <T> ObservableTransformer<T, List<Image>> open(final PickerConfig PickerConfig) {
        return new ObservableTransformer<T, List<Image>>() {
            @Override
            public ObservableSource<List<Image>> apply(@NonNull Observable<T> upstream) {
                return doImage(PickerConfig);
            }
        };
    }


    public Observable<List<Image>> doImage(PickerConfig PickerConfig) {
        //设置全局Config
        rxPhotoFragment.createSubject(RxPhotoFragment.ALBUM);
        rxPhotoFragment.openAlbum(PickerConfig);
        return rxPhotoFragment.getSubjectByConfig(RxPhotoFragment.ALBUM)
                .flatMap(new Function<RxPhotoFragment.Out, ObservableSource<List<Image>>>() {
                    @Override
                    public ObservableSource<List<Image>> apply(@NonNull RxPhotoFragment.Out out) {
                        return Observable.just(out.getSelectImages());
                    }
                });
    }

    public Observable<List<String>> doZip(final List<CompressInfo> compressInfo) {

        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<List<String>> e) {
                ImageZip.getInstance()
                        .zipImages(new ImageCompressor(),compressInfo, new CompressCallBack() {
                                    @Override
                                    public void onCompress(List<String> path) {
                                        e.onNext(path);
                                        e.onComplete();
                                    }
                                });
            }
        });
    }

    public Observable<String> doCrop(CropConfig cropConfig) {
        rxPhotoFragment.createSubject(RxPhotoFragment.CROP);
        rxPhotoFragment.openCrop(cropConfig);
        return rxPhotoFragment.getSubjectByConfig(RxPhotoFragment.CROP)
                .flatMap(new Function<RxPhotoFragment.Out, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull RxPhotoFragment.Out out) throws Exception {
                        return Observable.just(out.getCropPath());
                    }
                });
    }


}
