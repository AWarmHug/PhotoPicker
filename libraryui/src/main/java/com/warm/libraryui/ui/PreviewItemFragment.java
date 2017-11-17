package com.warm.libraryui.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.warm.library.find.bean.ImageBean;
import com.warm.libraryui.R;
import com.warm.libraryui.config.DataManager;


/**
 * 作者：warm
 * 时间：2017-11-11 11:08
 * 描述：
 */

public class PreviewItemFragment extends Fragment {

    private PhotoView pv;

    private ImageBean imageBean;

    public static PreviewItemFragment newInstance(ImageBean image) {

        Bundle args = new Bundle();
        args.putParcelable("image", image);
        PreviewItemFragment fragment = new PreviewItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBean = getArguments().getParcelable("image");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pv = (PhotoView) view.findViewById(R.id.pv);
        DataManager.getInstance().getILoader().load(pv, imageBean.getPath());
        pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PreviewFragment) getParentFragment()).anim();
            }
        });

    }
}
