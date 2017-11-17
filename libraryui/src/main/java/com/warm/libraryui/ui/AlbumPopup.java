package com.warm.libraryui.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warm.library.find.bean.AlbumBean;
import com.warm.libraryui.R;
import com.warm.libraryui.base.BasePopup;
import com.warm.libraryui.ui.adapter.AlbumAdapter;
import com.warm.libraryui.ui.adapter.OnItemSelectListener;
import com.warm.libraryui.ui.adapter.SimpleItemSelectListener;

import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-17 17:22
 * 描述：
 */

public class AlbumPopup extends BasePopup {

    private View view;


    private RecyclerView mAlbumRecy;

    private AlbumAdapter albumAdapter;

    public AlbumPopup(Context context, List<AlbumBean> albums, final OnItemSelectListener<AlbumBean> onItemClickListener){
        super(context);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.AppTheme_Popup);
        view= LayoutInflater.from(context).inflate(R.layout.popup_album,null,true);
        setContentView(view);

        mAlbumRecy = (RecyclerView) view.findViewById(R.id.recy_album);


        albumAdapter=new AlbumAdapter(albums);
        albumAdapter.setOnItemSelectListener(new SimpleItemSelectListener<AlbumBean>() {
            @Override
            public void itemClick(int position, AlbumBean albumBean) {
                onItemClickListener.itemClick(position, albumBean);
                dismiss();
            }
        });
        mAlbumRecy.setAdapter(albumAdapter);
        mAlbumRecy.setLayoutManager(new LinearLayoutManager(context));
    }

    public void setSelect(int position){
        if (albumAdapter!=null) {
            albumAdapter.setSelect(position);
        }
    }

    public AlbumBean getSelect(){
        return albumAdapter.getSelect();
    }




}
