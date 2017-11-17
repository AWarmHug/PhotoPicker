package com.warm.libraryui.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.warm.library.WorkExecutor;
import com.warm.library.crop.CropView;
import com.warm.library.zip.ZipAction;
import com.warm.libraryui.R;
import com.warm.libraryui.config.CropConfig;
import com.warm.libraryui.RxPhotoFragment;

import java.io.File;

/**
 * 作者：warm
 * 时间：2017-11-16 10:25
 * 描述：
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_CROP_CONFIG = "CropConfig";

    private CropView cropView;
    private Button sure;
    private CropConfig config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        config = getIntent().getParcelableExtra(KEY_CROP_CONFIG);


        cropView = (CropView) findViewById(R.id.cropView);
        sure = (Button) findViewById(R.id.bt_sure);
        sure.setOnClickListener(this);


        cropView.of(config.getImageUri());

        switch (config.getShape()) {
            case CIRCLE:
                //圆形，传入的参数，决定生成的图片是否是圆形，如果false 显示的是圆形，但实际生成的图片时正方形。类似QQ，qq显示的是圆形，但最终生成的图片还是正方形
                cropView.asCircle(false);
                break;
            case SQUARE:
                //正方形
                cropView.asSquare();
                break;
            case RECT:
                //按比例
                cropView.withAspect(config.getShow()[0], config.getShow()[1]);
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
                            ZipAction.getInstance().saveOutput(new File(config.getToPath()), cropView.getOutput(), 100, Bitmap.CompressFormat.JPEG);
                            finishBack();
                        }
                    });


        }
    }


    private void finishBack() {
        WorkExecutor.getInstance().runUi(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(RxPhotoFragment.KEY_CROP_IMAGE_PATH, config.getToPath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
