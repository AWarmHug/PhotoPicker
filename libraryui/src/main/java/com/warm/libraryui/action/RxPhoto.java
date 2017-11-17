package com.warm.libraryui.action;

import android.support.v7.app.AppCompatActivity;

import com.warm.library.find.bean.ImageBean;
import com.warm.library.zip.ImageZip;
import com.warm.library.zip.ZipCallBack;
import com.warm.library.zip.bean.ZipInfo;

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
                    .commitAllowingStateLoss();
        }
        return rxPhotoFragment;
    }

    private RxPhotoFragment findRxPhotoFragment(AppCompatActivity activity) {
        return (RxPhotoFragment) activity.getSupportFragmentManager().findFragmentByTag(RxPhotoFragment.class.getSimpleName());
    }


    public <T> ObservableTransformer<T, List<ImageBean>> open(final Config config) {
        return new ObservableTransformer<T, List<ImageBean>>() {
            @Override
            public ObservableSource<List<ImageBean>> apply(@NonNull Observable<T> upstream) {
                return doImage(config);
            }
        };
    }


    public Observable<List<ImageBean>> doImage(Config config) {
        //设置全局Config
        DataManager.getInstance().setConfig(config);
        rxPhotoFragment.createSubject(RxPhotoFragment.ALBUM);
        rxPhotoFragment.openAlbum();
        return rxPhotoFragment.getSubjectByConfig(RxPhotoFragment.ALBUM)
                .flatMap(new Function<RxPhotoFragment.Out, ObservableSource<List<ImageBean>>>() {
                    @Override
                    public ObservableSource<List<ImageBean>> apply(@NonNull RxPhotoFragment.Out out) throws Exception {
                        return Observable.just(out.getSelectImages());
                    }
                });
    }

    public Observable<List<ImageBean>> doZip(final List<ZipInfo> zipInfo) {
        return Observable.create(new ObservableOnSubscribe<List<ImageBean>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<List<ImageBean>> e) throws Exception {
                ImageZip.getInstance()
                        .zipImages(rxPhotoFragment.getContext().getContentResolver()
                                , zipInfo
                                , new ZipCallBack() {
                                    @Override
                                    public void onFinish(List<ImageBean> imageBean) {
                                        e.onNext(imageBean);
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