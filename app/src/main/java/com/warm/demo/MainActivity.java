package com.warm.demo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.warm.picker.find.entity.Image;
import com.warm.picker.zip.bean.ZipInfo;
import com.warm.pickerui.config.CropConfig;
import com.warm.pickerui.config.PickerConfig;
import com.warm.pickerui.rx.RxPhoto;
import com.warm.pickerui.ui.adapter.ContentAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 作者：warm
 * 时间：2017-10-18 11:21
 * 描述：
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity--";

    public static final int REQUEST_MANY = 3;

    private ContentAdapter mAdapter;
    private RecyclerView mContent;
    private Button btOne;
    private Button btOneCrop;
    private Button btOneCropZip;
    private Button btManyZip;
    private Button btMany;
    private RxPermissions mRxPermissions;
    private RxPhoto mRxPhoto;
    private ProgressDialog pDialog;

    public ProgressDialog getPDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this, R.style.AppTheme_Dialog_Progress);
            pDialog.setMessage("请稍后...");
            pDialog.setCancelable(false);
            pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        onBackPressed();
                        return true;
                    }
                    return false;
                }
            });
        }
        return pDialog;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btOne = (Button) findViewById(R.id.bt_one);
        btOne.setOnClickListener(this);

        btOneCrop = (Button) findViewById(R.id.bt_one_crop);
        btOneCrop.setOnClickListener(this);

        btOneCropZip = (Button) findViewById(R.id.bt_one_crop_zip);
        btOneCropZip.setOnClickListener(this);

        btMany = (Button) findViewById(R.id.bt_many);
        btMany.setOnClickListener(this);

        btManyZip = (Button) findViewById(R.id.bt_many_zip);
        btManyZip.setOnClickListener(this);

        List<Image> list = new ArrayList<>();
        mAdapter = new ContentAdapter(list);
        mAdapter.setNeedHeader(false);
        mContent = (RecyclerView) findViewById(R.id.content_list);
        mContent.setAdapter(mAdapter);
        mContent.setLayoutManager(new GridLayoutManager(this, 3));

        mRxPhoto = new RxPhoto(this);
        mRxPermissions = new RxPermissions(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_one:
                mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .flatMap(new Function<Boolean, ObservableSource<List<Image>>>() {
                            @Override
                            public ObservableSource<List<Image>> apply(@NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    return mRxPhoto.doImage(new PickerConfig().setMaxSelectNum(1));
                                } else {
                                    return Observable.error(new Throwable("没有权限"));
                                }
                            }
                        })
                        .subscribe(new Consumer<List<Image>>() {
                            @Override
                            public void accept(@NonNull List<Image> imageBeen) throws Exception {
                                mAdapter.refreshAll(imageBeen);
                            }
                        });
                break;
            case R.id.bt_one_crop:
                mRxPhoto.doImage(new PickerConfig().setMaxSelectNum(1))
                        .flatMap(new Function<List<Image>, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(@NonNull List<Image> imageBeen) throws Exception {
                                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "crop" + imageBeen.get(0).getName());
                                CropConfig cropConfig = new CropConfig(CropConfig.RECT, imageBeen.get(0).getUri(), file.getPath());
                                return mRxPhoto.doCrop(cropConfig);
                            }
                        })
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(@NonNull String s) throws Exception {
                                Image bean = new Image();
                                bean.setData(s);
                                mAdapter.insertTopData(bean);

                                Log.d(TAG, "accept: path=" + s);
                            }
                        });

                break;
            case R.id.bt_one_crop_zip:
                mRxPhoto.doImage(new PickerConfig().setMaxSelectNum(1))
                        .flatMap(new Function<List<Image>, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(@NonNull List<Image> imageBeen) throws Exception {
                                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "crop" + imageBeen.get(0).getName());
                                CropConfig cropConfig = new CropConfig(CropConfig.SQUARE, imageBeen.get(0).getUri(), file.getPath());
                                cropConfig.setShow(2, 1);
                                return mRxPhoto.doCrop(cropConfig);
                            }
                        })
                        .flatMap(new Function<String, ObservableSource<List<String>>>() {
                            @Override
                            public ObservableSource<List<String>> apply(@NonNull String s) throws Exception {
                                getPDialog().show();
                                List<ZipInfo> zipInfos = new ArrayList<>();
                                File file = new File(s);
                                ZipInfo zipInfo = new ZipInfo(s
                                        , getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + File.separator + "zip" + file.getName()
                                        , 100 * 1024);
                                zipInfos.add(zipInfo);
                                return mRxPhoto.doZip(zipInfos);
                            }
                        })
                        .map(new Function<List<String>, List<Image>>() {
                            @Override
                            public List<Image> apply(@NonNull List<String> list) throws Exception {
                                List<Image> images = new ArrayList<Image>(list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    Image image = new Image(0, list.get(1));
                                    images.add(image);
                                }
                                return images;
                            }
                        })
                        .subscribe(new Consumer<List<Image>>() {
                            @Override
                            public void accept(@NonNull List<Image> imageBeen) throws Exception {
                                getPDialog().dismiss();
                                mAdapter.insertRange(imageBeen);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });

                break;
            case R.id.bt_many:

                mRxPhoto.doImage(new PickerConfig().setMaxSelectNum(9))
                        .subscribe(new Consumer<List<Image>>() {
                            @Override
                            public void accept(@NonNull List<Image> imageBeen) throws Exception {
                                mAdapter.refreshAll(imageBeen);
                            }
                        });
                break;

            case R.id.bt_many_zip:
                mRxPhoto.doImage(new PickerConfig().setMaxSelectNum(9))
                        .map(new Function<List<Image>, List<Image>>() {
                            @Override
                            public List<Image> apply(@NonNull List<Image> imageBeens) throws Exception {
                                mAdapter.refreshAll(imageBeens);
                                return imageBeens;
                            }
                        })
                        .flatMap(new Function<List<Image>, ObservableSource<List<Image>>>() {
                            @Override
                            public ObservableSource<List<Image>> apply(@NonNull List<Image> images) throws Exception {
                                getPDialog().show();

                                List<ZipInfo> zipInfos = new ArrayList<>();
                                for (int i = 0; i < images.size(); i++) {
                                    File file = new File(images.get(i).getData());

                                    ZipInfo zipInfo = new ZipInfo(images.get(i).getData()
                                            , getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + File.separator + "zip" + File.separator + file.getName()
                                            /*, 100, 100*/, 100 * 1024);
                                    zipInfos.add(zipInfo);
                                }
                                return mRxPhoto.doZip(zipInfos)
                                        .flatMap(new Function<List<String>, ObservableSource<List<Image>>>() {
                                            @Override
                                            public ObservableSource<List<Image>> apply(@NonNull List<String> list) throws Exception {
                                                List<Image> images = new ArrayList<>(list.size());
                                                for (int i = 0; i < list.size(); i++) {
                                                    Log.d(TAG, "apply: " + list.get(i));
                                                    Image image = new Image(0, list.get(i));
                                                    images.add(image);
                                                }
                                                return Observable.just(images);
                                            }
                                        });
                            }
                        })
                        .subscribe(new Consumer<List<Image>>() {
                            @Override
                            public void accept(@NonNull List<Image> images) throws Exception {
                                Log.d(TAG, "accept: "+images.get(0).getData());
                                getPDialog().dismiss();
                                mAdapter.insertRange(images);
                            }
                        });
                break;

        }
    }

}
