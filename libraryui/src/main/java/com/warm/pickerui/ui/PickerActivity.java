package com.warm.pickerui.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.warm.picker.WorkExecutor;
import com.warm.picker.find.entity.Album;
import com.warm.picker.find.entity.Image;
import com.warm.picker.find.callback.AlbumFindCallBack;
import com.warm.picker.find.callback.ImageFindCallBack;
import com.warm.picker.find.work.AlbumFind;
import com.warm.picker.find.work.ImageFind;
import com.warm.picker.zip.ZipAction;
import com.warm.pickerui.DataManager;
import com.warm.pickerui.R;
import com.warm.pickerui.config.PickerConfig;
import com.warm.pickerui.rx.RxPhotoFragment;
import com.warm.pickerui.ui.adapter.ContentAdapter;
import com.warm.pickerui.ui.adapter.SimpleItemSelectListener;
import com.warm.pickerui.weidget.GridItemDecoration;

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
    private static final String TAG = "photoPickerTAG";

    public static final int TO_CAMERA = 1;
    //权限请求中的requestCode必须大于0
    public static final int REQUEST_CODE_PICK = 100;


    public static final int REQUEST_CODE_CAMERA = 101;

    public static final String KEY_PICKER_CONFIG = "key_picker_config";
    public static final String KEY_ALBUMS = "albums";
    public static final String KEY_CURRENT_ALBUM_POSITION = "current_album_position";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_SELECT_IMAGES = "selectImages";


    private RecyclerView mList;
    private TextView tvAlbum;
    private Button mPreview;

    private AlbumDialog mAlbumDialog;

    private long time;
    private String cameraPath;
    private ContentAdapter mContentAdapter;

    private PickerConfig mPickerConfig;

    private Button btSure;

    private List<Album> mAlbums;

    private int mCurrentAlbumPosition;

    private List<Image> mImages;

    private List<Image> mSelectImages;


    public List<Image> getAllImages() {
        if (mContentAdapter == null || mContentAdapter.getList() == null) {
            return new ArrayList<>();
        } else {
            return mContentAdapter.getList();
        }
    }

    public List<Image> getSelectImages() {
        if (mContentAdapter == null || mContentAdapter.getSelectedImages() == null) {
            return new ArrayList<>();
        } else {
            return mContentAdapter.getSelectedImages();
        }
    }

    public PickerConfig getConfig() {
        Log.d(TAG, "getConfig: ");
        return mPickerConfig;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btSure = (Button) findViewById(R.id.bt_sure);
        btSure.setOnClickListener(this);
        mList = (RecyclerView) findViewById(R.id.content_list);
        tvAlbum = (TextView) findViewById(R.id.album);
        mPreview = (Button) findViewById(R.id.preview);
        tvAlbum.setOnClickListener(this);
        mPreview.setOnClickListener(this);

        Log.d(TAG, "PickerActivity--onCreate: ");
        if (savedInstanceState != null) {
            mPickerConfig = savedInstanceState.getParcelable(KEY_PICKER_CONFIG);
        } else {
            mPickerConfig = getIntent().getParcelableExtra(KEY_PICKER_CONFIG);
        }

        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PICK);
        } else {
            findAlbum(savedInstanceState);
        }
    }


    private void findAlbum(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "findAlbum: savedInstanceState!=null");
            mAlbums = savedInstanceState.getParcelableArrayList(KEY_ALBUMS);
            mCurrentAlbumPosition = savedInstanceState.getInt(KEY_CURRENT_ALBUM_POSITION);
            mImages = savedInstanceState.getParcelableArrayList(KEY_IMAGES);
            mSelectImages = savedInstanceState.getParcelableArrayList(KEY_SELECT_IMAGES);
            Log.d(TAG, "findAlbum: " + mImages.get(1).isSelected());

            setAlbumUi(mAlbums, true);
            setImagesUi();

        } else {
            _findAlbum();
        }

    }

    private void _findAlbum() {
        AlbumFind.getInstance().findAlbum(getContentResolver(), new AlbumFindCallBack() {
            @Override
            public void albumFind(List<Album> albums) {
                mAlbums = albums;
                setAlbumUi(albums, false);
            }
        });
    }


    private void setAlbumUi(List<Album> albums, boolean restore) {
        mAlbumDialog = new AlbumDialog(PickerActivity.this, albums, simpleItemSelectListener);
        tvAlbum.setEnabled(true);
        if (!restore) {
            simpleItemSelectListener.itemClick(0, albums.get(0));
        }
    }

    private void onPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PICK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _findAlbum();
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

                WorkExecutor.getInstance().runWorker(new Runnable() {
                    @Override
                    public void run() {
                        Image image;
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        //负责加载图片但是不保存到内存中,
                        options.inJustDecodeBounds = true;
                        final File file = new File(cameraPath);
                        BitmapFactory.decodeFile(cameraPath, options);
                        Uri uri = ZipAction.getInstance().saveContentProvider(getContentResolver(), file, options, time);
                        if (uri != null) {
                            image = ImageFind.getInstance().findImageByUri(getContentResolver(), uri);
                        } else {
                            Log.d(TAG, "run: 插入错误");
                            image = new Image();
                            image.setData(cameraPath);
                        }
                        addCameraImage(image);
                    }
                });
            }
        }
    }

    private void addCameraImage(final Image image) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                mContentAdapter.setNeedHeader(true);
                mContentAdapter.insertItem(0, image);
            }
        });

    }

    private SimpleItemSelectListener<Album> simpleItemSelectListener = new SimpleItemSelectListener<Album>() {


        @Override
        public void itemClick(final int position, final Album album) {
            mCurrentAlbumPosition = position;
            ImageFind.getInstance()
                    .findImage(getContentResolver(), album.getBucketId(), new ImageFindCallBack() {
                        @Override
                        public void imageFind(final List<Image> images) {
                            mImages = images;
                            setImagesUi();
                        }
                    });
        }

    };

    private void setImagesUi() {
        tvAlbum.setText(mAlbums.get(mCurrentAlbumPosition).getBucketName());

        mAlbumDialog.setSelect(mCurrentAlbumPosition);
        if (mContentAdapter == null) {
            mContentAdapter = new ContentAdapter(mImages, true);
            mContentAdapter.setNeedHeader(true);
            mList.setAdapter(mContentAdapter);
            mList.setLayoutManager(new GridLayoutManager(PickerActivity.this, 3));
            mList.addItemDecoration(new GridItemDecoration(getResources().getDimensionPixelOffset(R.dimen.grid_space), 3));
        } else {
            checkImages(mImages, mContentAdapter.getSelectedImages());
            if (mCurrentAlbumPosition == 0) {
                mContentAdapter.setNeedHeader(true);
            } else {
                mContentAdapter.setNeedHeader(false);
            }
            mContentAdapter.refreshAll(mImages);
            mList.getLayoutManager().scrollToPosition(0);
        }
        if (mSelectImages != null && mSelectImages.size() != 0) {
            mContentAdapter.setSelects(mSelectImages);
        }

        mContentAdapter.setOnItemSelectListener(new SimpleItemSelectListener<Image>() {
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
            public void itemSelect(int position, Image image) {

                if (mContentAdapter.getSelectedImages().size() >= mPickerConfig.getMaxSelectNum() && !image.isSelected()) {
                    Toast.makeText(PickerActivity.this, "最多选择" + mPickerConfig.getMaxSelectNum() + "张", Toast.LENGTH_SHORT).show();
                } else {
                    if (!image.isSelected()) {
                        mContentAdapter.checkedAdd(position);
                    } else {
                        mContentAdapter.checkedRemove(position);
                    }
                    image.setSelected(!image.isSelected());
                    mContentAdapter.refreshItem(position);
                }
                setUiChange();
            }

            @Override
            public void itemClick(int position, Image image) {
                goPreview(position, true);
            }
        });
        setUiChange();
    }

    private void setUiChange() {

        if (mContentAdapter.getSelectedImages().size() != 0) {
            btSure.setEnabled(true);
            btSure.setText(String.format(Locale.getDefault(), "选中(%d/%d)", mContentAdapter.getSelectedImages().size(), mPickerConfig.getMaxSelectNum()));
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
    private void checkImages(List<Image> allImages, List<Image> selectedImages) {
        if (allImages == null || allImages.size() == 0
                || selectedImages == null || selectedImages.size() == 0) {
            return;
        }
        Map<String, Image> map = new HashMap<>(allImages.size());
        for (Image allMedia : allImages) {
            allMedia.setSelected(false);
            map.put(allMedia.getData(), allMedia);
        }

        for (Image media : selectedImages) {
            if (map.containsKey(media.getData())) {
                map.get(media.getData()).setSelected(true);
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
                .setCustomAnimations(R.anim.picker_fragment_enter, 0, 0, R.anim.picker_fragment_exit)
                .add(R.id.content, PreviewFragment.newInstance(position, isAll))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.bt_sure) {
            intent.putParcelableArrayListExtra(RxPhotoFragment.KEY_SELECT_IMAGES, (ArrayList<Image>) mContentAdapter.getSelectedImages());
            setResult(RESULT_OK, intent);
            finish();
        } else if (i == R.id.album) {
            if (mAlbumDialog.isShowing()) {
                mAlbumDialog.dismiss();
            } else {
                mAlbumDialog.reset();
                mAlbumDialog.show();
            }

        } else if (i == R.id.preview) {
            goPreview(0, false);
        }
    }

    public void backInfo() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(RxPhotoFragment.KEY_SELECT_IMAGES, (ArrayList<Image>) mContentAdapter.getSelectedImages());
        setResult(RESULT_OK, intent);
        finish();
    }


    private void openCamera() {
        File parentFile = checkParent(DataManager.getInstance().getConfig().getCameraDir());
        if (parentFile != null) {
            time = System.currentTimeMillis();
            File file = new File(parentFile, String.format(Locale.getDefault(), "IMG_%d.jpg", time));
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putParcelable(KEY_PICKER_CONFIG, mPickerConfig);
        outState.putParcelableArrayList(KEY_ALBUMS, (ArrayList<? extends Parcelable>) mAlbums);
        outState.putInt(KEY_CURRENT_ALBUM_POSITION, mCurrentAlbumPosition);
        outState.putParcelableArrayList(KEY_IMAGES, (ArrayList<? extends Parcelable>) mContentAdapter.getList());
        outState.putParcelableArrayList(KEY_SELECT_IMAGES, (ArrayList<? extends Parcelable>) mContentAdapter.getSelectedImages());
    }


}
