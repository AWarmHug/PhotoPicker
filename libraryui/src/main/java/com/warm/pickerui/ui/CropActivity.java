package com.warm.pickerui.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.warm.picker.WorkExecutor;
import com.warm.picker.crop.CropView;
import com.warm.picker.zip.ZipAction;
import com.warm.pickerui.R;
import com.warm.pickerui.config.CropConfig;
import com.warm.pickerui.rx.RxPhotoFragment;

import java.io.File;

/**
 * 作者：warm
 * 时间：2017-11-16 10:25
 * 描述：
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "photoPickerTAG";
    public static final String KEY_CROP_INFO = "CropConfig";
    private Toolbar tb;
    private CropView cropView;
    private Button sure;
    private CropConfig mCropConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (savedInstanceState != null) {
            mCropConfig = savedInstanceState.getParcelable(KEY_CROP_INFO);
        } else {
            mCropConfig = getIntent().getParcelableExtra(KEY_CROP_INFO);
        }
        tb = (Toolbar) findViewById(R.id.tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cropView = (CropView) findViewById(R.id.cropView);
        sure = (Button) findViewById(R.id.bt_sure);
        sure.setOnClickListener(this);


        cropView.of(mCropConfig.getImageUri());

        switch (mCropConfig.getShape()) {
            case CropConfig.CIRCLE:
                //圆形，传入的参数，决定生成的图片是否是圆形，如果false 显示的是圆形，但实际生成的图片时正方形。类似QQ，qq显示的是圆形，但最终生成的图片还是正方形
                cropView.asCircle(false);
                break;
            case CropConfig.SQUARE:
                //正方形
                cropView.asSquare();
                break;
            case CropConfig.RECT:
                //按比例
                cropView.withAspect(mCropConfig.getShow()[0], mCropConfig.getShow()[1]);
                break;
        }
        cropView.initialize(this);
        sure.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_sure) {
            WorkExecutor.getInstance()
                    .runWorker(new Runnable() {
                        @Override
                        public void run() {
                            boolean success;
                            Bitmap bitmap = cropView.getOutput();
                            if (bitmap==null){
                                success=false;
                            }else {
                                success = ZipAction.getInstance().saveOutput(new File(mCropConfig.getToPath()), bitmap, 100, Bitmap.CompressFormat.JPEG);
                                bitmap.recycle();
                            }
                            finishBack(success);

                        }
                    });
        }
    }


    private void finishBack(final boolean success) {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if (success) {
                    intent.putExtra(RxPhotoFragment.KEY_CROP_IMAGE_PATH, mCropConfig.getToPath());
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CROP_INFO, mCropConfig);
    }
}
