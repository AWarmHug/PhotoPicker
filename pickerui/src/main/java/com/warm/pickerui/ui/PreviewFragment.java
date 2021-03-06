package com.warm.pickerui.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.warm.picker.pick.entity.Image;
import com.warm.pickerui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者：warm
 * 时间：2017-11-14 13:23
 * 描述：
 */

public class PreviewFragment extends Fragment {
    private static final String TAG = "photoPickerTAG";


    private Toolbar tb;

    private Button btSure;

    private int mPosition;
    private ViewPager pager;
    private ImageButton ib;
    private FrameLayout flSure;
    private int max;
    private boolean isAll;
    private List<Image> allImages;
    private List<Image> selectImages;


    public static PreviewFragment newInstance(int position) {
        return newInstance(position, true);
    }

    public static PreviewFragment newInstance(int position, boolean isAll) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putBoolean("isAll", isAll);
        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PreviewFragment---onCreate: ");
        mPosition = getArguments().getInt("position");
        isAll = getArguments().getBoolean("isAll");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof PickerActivity) {
            tb = getActivity().findViewById(R.id.tb);
            btSure = getActivity().findViewById(R.id.bt_sure);
        }

        view.setBackgroundColor(Color.BLACK);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        pager = view.findViewById(R.id.pager);
        ib = view.findViewById(R.id.ib);
        flSure = view.findViewById(R.id.fl_sure);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: ");
        //因为数据都是来源于PickerActivity，如果Activity恢复了，那么fragment就不需要恢复。

        if (getActivity() instanceof PickerActivity) {
            PickerActivity pickerActivity = (PickerActivity) getActivity();

            if (isAll) {
                allImages = pickerActivity.getAllImages();
            } else {
                allImages = new ArrayList<>();
                allImages.addAll(pickerActivity.getSelectImages());
            }
            selectImages = pickerActivity.getSelectImages();
            max = pickerActivity.getConfig().getMaxSelectNum();
            showDetail(allImages, selectImages);
        }

    }

    private void showDetail(final List<Image> images, final List<Image> selects) {

        pager.setAdapter(new Adapter(getChildFragmentManager(), images));

        pager.setCurrentItem(mPosition, false);

        setTb();

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPosition = position;
                setTb();
            }
        });
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images.get(mPosition).isSelected()) {
                    //已选择
                    if (selects.contains(images.get(mPosition))) {
                        selects.remove(images.get(mPosition));
                    }
                    images.get(mPosition).setSelected(false);
                } else {
                    if (selects.size() >= max) {
                        Toast.makeText(getActivity(), String.format(Locale.CHINA, "最多选中%d张", max), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //未选择
                    if (!selects.contains(images.get(mPosition))) {
                        selects.add(images.get(mPosition));
                    }
                    images.get(mPosition).setSelected(true);
                }
                setTb();
            }
        });
    }


    /**
     * 修改Toolbar显示内容
     */
    private void setTb() {

        Log.d(TAG, "setTb: ");
        if (allImages == null || allImages.size() == 0) {
            return;
        }
        tb.setTitle(String.format(Locale.getDefault(), "%d/%d", mPosition + 1, allImages.size()));
        ib.setSelected(allImages.get(mPosition).isSelected());
        if (selectImages.size() != 0) {
            btSure.setEnabled(true);
            btSure.setText(String.format(Locale.getDefault(), "选中(%d/%d)", selectImages.size(), max));
        } else {
            btSure.setEnabled(false);
            btSure.setText("选中");
        }
    }


    /**
     * Toolbar 显示或者隐藏
     */
    public void anim() {
        if (tb.getTranslationY() == 0) {
            tb.animate().translationY(-tb.getHeight()).setDuration(300).start();
            flSure.animate().translationY(flSure.getHeight()).setDuration(300).start();
        } else {
            tb.animate().translationY(0).setDuration(300).start();
            flSure.animate().translationY(0).setDuration(300).start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //将toolbar恢复原样
        if (tb.getTag() != null) {
            tb.setTitle((CharSequence) tb.getTag());
        }
        if (tb.getTranslationY() != 0) {
            tb.animate().translationY(0).setDuration(150).start();
            flSure.animate().translationY(0).setDuration(150).start();
        }
    }

    private class Adapter extends FragmentStatePagerAdapter {
        private List<Image> imageBeen;

        Adapter(FragmentManager fm, List<Image> imageBeen) {
            super(fm);
            this.imageBeen = imageBeen;
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewItemFragment.newInstance(imageBeen.get(position));
        }

        @Override
        public int getCount() {
            return imageBeen.size();
        }
    }


    /**
     * @return statuBar高度
     */
    public int getStateBarHeight() {
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight = 0;

        if (Build.VERSION.SDK_INT >= 21) {
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
