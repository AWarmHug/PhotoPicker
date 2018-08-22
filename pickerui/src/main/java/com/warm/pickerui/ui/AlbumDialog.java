package com.warm.pickerui.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.warm.picker.pick.entity.Album;
import com.warm.pickerui.R;
import com.warm.pickerui.ui.adapter.AlbumAdapter;
import com.warm.pickerui.ui.adapter.OnItemSelectListener;
import com.warm.pickerui.ui.adapter.SimpleItemSelectListener;
import com.warm.pickerui.utils.ScreenUtils;
import com.warm.pickerui.weidget.LineItemDecoration;

import java.util.List;

/**
 * 作者：warm
 * 时间：2018-08-03 14:33
 * 描述：
 */
public class AlbumDialog extends BottomSheetDialog {


    private RecyclerView mAlbumRecy;

    private AlbumAdapter albumAdapter;


    public AlbumDialog(@NonNull Context context, List<Album> albums, final OnItemSelectListener<Album> onItemClickListener) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_album, null, true);
        setContentView(view);

        mAlbumRecy = view.findViewById(R.id.recy_album);


        albumAdapter = new AlbumAdapter(albums);
        albumAdapter.setOnItemSelectListener(new SimpleItemSelectListener<Album>() {
            @Override
            public void itemClick(int position, Album album) {
                onItemClickListener.itemClick(position, album);
                dismiss();
            }
        });
        mAlbumRecy.setAdapter(albumAdapter);
        mAlbumRecy.setLayoutManager(new LinearLayoutManager(context));
        mAlbumRecy.addItemDecoration(new LineItemDecoration(context,LinearLayoutManager.VERTICAL, ScreenUtils.dp2px(context,1), Color.parseColor("#FFDCDCDC")));
    }

    public void reset(){
        mAlbumRecy.scrollToPosition(0);
    }

    public void setSelect(int position) {
        if (albumAdapter != null) {
            albumAdapter.setSelect(position);
        }
    }

    public Album getSelect() {
        return albumAdapter.getSelect();
    }


}
