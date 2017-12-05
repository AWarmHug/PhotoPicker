package com.warm.libraryui.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.warm.library.WorkExecutor;
import com.warm.library.find.bean.AlbumBean;
import com.warm.library.find.bean.ImageBean;
import com.warm.library.find.callback.AlbumFindCallBack;
import com.warm.library.find.callback.ImageFindCallBack;
import com.warm.library.find.work.AlbumFind;
import com.warm.library.find.work.ImageFind;
import com.warm.library.zip.ZipAction;
import com.warm.libraryui.DataManager;
import com.warm.libraryui.R;
import com.warm.libraryui.RxPhotoFragment;
import com.warm.libraryui.config.PickerConfig;
import com.warm.libraryui.ui.adapter.ContentAdapter;
import com.warm.libraryui.ui.adapter.SimpleItemSelectListener;
import com.warm.libraryui.weidget.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 作者：warm
 * 时间：2017-10-16 14:05
 * 描述：
 */

public class PickerActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TO_CAMERA = 1;

    //权限请求中的requestCode必须大于0
    public static final int REQUEST_CODE_PICK = 100;
    public static final int REQUEST_CODE_CAMERA = 101;


    private static final String TAG = "PickerActivity--";

    public static final String KEY_PICKER_CONFIG = "key_picker_config";

    private RecyclerView mContent;
    private TextView mAlbum;
    private Button mPreview;

    private AlbumPopup albumPopup;

    private long time;
    private String cameraPath;
    private ContentAdapter mContentAdapter;

    private PickerConfig mPickerConfig;

    private Button btSure;


    public List<ImageBean> getAllImages() {
        if (mContentAdapter == null || mContentAdapter.getList() == null) {
            return new ArrayList<>();
        } else {
            return mContentAdapter.getList();
        }
    }

    public List<ImageBean> getSelectImages() {
        if (mContentAdapter == null || mContentAdapter.getSelectedImages() == null) {
            return new ArrayList<>();
        } else {
            return mContentAdapter.getSelectedImages();
        }
    }

    public PickerConfig getConfig() {
        return mPickerConfig;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mPickerConfig = getIntent().getParcelableExtra(KEY_PICKER_CONFIG);

        btSure = (Button) findViewById(R.id.bt_sure);
        btSure.setOnClickListener(this);
        mContent = (RecyclerView) findViewById(R.id.content_list);
        mAlbum = (TextView) findViewById(R.id.album);
        mPreview = (Button) findViewById(R.id.preview);
        mAlbum.setOnClickListener(this);
        mPreview.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PICK);
        } else {
            findAlbum();
        }
    }

    private void findAlbum() {
        AlbumFind.getInstance().findAlbum(getContentResolver(), new AlbumFindCallBack() {
            @Override
            public void albumFind(List<AlbumBean> albums) {

                albumPopup = new AlbumPopup(PickerActivity.this, albums, simpleItemSelectListener);

                simpleItemSelectListener.itemClick(0, albums.get(0));
                mAlbum.setEnabled(true);
            }
        });
    }

    private void onPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PICK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findAlbum();
                } else {
                    Toast.makeText(this, "请打开文件查看权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "请打开拍照权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPermission(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TO_CAMERA) {
            if (cameraPath != null) {


                final BitmapFactory.Options options = new BitmapFactory.Options();
                //负责加载图片但是不保存到内存中,
                options.inJustDecodeBounds = true;
                final File file = new File(cameraPath);
                BitmapFactory.decodeFile(cameraPath, options);
//                try {
//                    MediaStore.Images.Media.insertImage(getContentResolver(),cameraPath,"sss","ssss");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
                WorkExecutor.getInstance().runWorker(new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = ZipAction.getInstance().saveContentProvider(getContentResolver(), file, options, time);
                        if (uri != null) {
                            ImageBean imageBean = ImageFind.getInstance().findImageByUri(getContentResolver(), uri);
                            addCameraImage(imageBean);
                        } else {
                            Log.d(TAG, "run: 插入错误");
                            ImageBean imageBean = new ImageBean();
                            imageBean.setPath(cameraPath);
                            addCameraImage(imageBean);
                        }
                    }
                });
            }
        }
    }

    private void addCameraImage(final ImageBean imageBean) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                mContentAdapter.setNeedHeader(true);
                mContentAdapter.insertItem(0, imageBean);

            }
        });

    }

    private SimpleItemSelectListener<AlbumBean> simpleItemSelectListener = new SimpleItemSelectListener<AlbumBean>() {


        @Override
        public void itemClick(final int position, final AlbumBean albumBean) {
            ImageFind.getInstance()
                    .findImage(getContentResolver(), albumBean.getBucketId(), new ImageFindCallBack() {
                        @Override
                        public void imageFind(final List<ImageBean> images) {
                            Log.d(TAG, "imageFind: size=" + images.size() + images.toString());
                            mAlbum.setText(albumBean.getBucketName());

                            albumPopup.setSelect(position);
                            if (mContentAdapter == null) {
                                mContentAdapter = new ContentAdapter(images, true);
                                mContentAdapter.setNeedHeader(true);
                                mContent.setAdapter(mContentAdapter);
                                mContent.setLayoutManager(new GridLayoutManager(PickerActivity.this, 3));
                                mContent.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelOffset(R.dimen.grid_space), 3));
                            } else {
                                checkImages(images, mContentAdapter.getSelectedImages());
                                if (position == 0) {
                                    mContentAdapter.setNeedHeader(true);
                                } else {
                                    mContentAdapter.setNeedHeader(false);
                                }
                                mContentAdapter.refreshAll(images);
                                mContent.getLayoutManager().scrollToPosition(0);
                            }

                            mContentAdapter.setOnItemSelectListener(new SimpleItemSelectListener<ImageBean>() {
                                @Override
                                public void cameraClick() {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                                        } else {
                                            openCamera();
                                        }
                                    } else {
                                        openCamera();
                                    }

                                }

                                @Override
                                public void itemSelect(int position, ImageBean imageBean) {

                                    if (mContentAdapter.getSelectedImages().size() >= mPickerConfig.getMaxSelectNum() && !imageBean.isSelected()) {
                                        Toast.makeText(PickerActivity.this, "最多选择" + mPickerConfig.getMaxSelectNum() + "张", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!imageBean.isSelected()) {
                                            mContentAdapter.checkedAdd(position);
                                        } else {
                                            mContentAdapter.checkedRemove(position);
                                        }
                                        imageBean.setSelected(!imageBean.isSelected());
                                        mContentAdapter.refreshItem(position);
                                    }
                                    setUiChange();
                                }

                                @Override
                                public void itemClick(int position, ImageBean imageBean) {
                                    goPreview(position, true);
                                }
                            });
                            setUiChange();
                        }
                    });
        }

    };

    private void setUiChange() {

        if (mContentAdapter.getSelectedImages().size() != 0) {
            btSure.setEnabled(true);
            btSure.setText(String.format(Locale.CHINA, "选中(%d/%d)", mContentAdapter.getSelectedImages().size(), mPickerConfig.getMaxSelectNum()));
            mPreview.setEnabled(true);
        } else {
            btSure.setEnabled(false);
            btSure.setText("选中");
            mPreview.setEnabled(false);
        }
    }


    /**
     * 因为每次都会重新刷新一遍，所以每次刷新界面前都需要把之前纯在Path检查一遍
     * 检查是否选择的images中
     */
    private void checkImages(List<ImageBean> allImages, List<ImageBean> selectedImages) {
        if (allImages == null || allImages.size() == 0
                || selectedImages == null || selectedImages.size() == 0) {
            return;
        }
        Map<String, ImageBean> map = new HashMap<>(allImages.size());
        for (ImageBean allMedia : allImages) {
            allMedia.setSelected(false);
            map.put(allMedia.getPath(), allMedia);
        }

        for (ImageBean media : selectedImages) {
            if (map.containsKey(media.getPath())) {
                map.get(media.getPath()).setSelected(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            if (mContentAdapter != null) {
                checkImages(mContentAdapter.getList(), mContentAdapter.getSelectedImages());
                mContentAdapter.notifyDataSetChanged();
                setUiChange();
            }
        }
        super.onBackPressed();
    }

    private void goPreview(int position, boolean isAll) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.my_abc_popup_enter, 0, 0, R.anim.my_abc_popup_exit)
                .add(R.id.content, PreviewFragment.newInstance(position, isAll))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.bt_sure) {
            intent.putParcelableArrayListExtra(RxPhotoFragment.KEY_SELECT_IMAGES, (ArrayList<ImageBean>) mContentAdapter.getSelectedImages());
            setResult(RESULT_OK, intent);
            finish();


        } else if (i == R.id.album) {
            if (albumPopup.isShowing()) {
                albumPopup.dismiss();
            } else {
                albumPopup.showAtLocation(mContent, Gravity.BOTTOM, 0, 0);
            }

        } else if (i == R.id.preview) {
            goPreview(0, false);

        }
    }

    public void backInfo() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(RxPhotoFragment.KEY_SELECT_IMAGES, (ArrayList<ImageBean>) mContentAdapter.getSelectedImages());
        setResult(RESULT_OK, intent);
        finish();
    }


    private void openCamera() {
        File parentFile = checkParent(DataManager.getInstance().getConfig().getCameraDir());
        if (parentFile != null) {
            time = System.currentTimeMillis();
            File file = new File(parentFile, "IMG_" + time + ".jpg");
            Uri photoUri;
            cameraPath = file.getPath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                photoUri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
            startActivityForResult(intent, TO_CAMERA);

        }
    }

    private File checkParent(String path) {
        //判断sd卡情况
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !Environment.isExternalStorageRemovable()) {
            File parentFile = new File(path);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            return parentFile;
        } else {
            return null;
        }
    }


}
