package com.warm.pickerui.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.warm.picker.pick.entity.Album;
import com.warm.picker.pick.entity.Image;
import com.warm.pickerui.R;
import com.warm.pickerui.config.PickerUI;
import com.warm.pickerui.ui.base.BaseAdapter;
import com.warm.pickerui.ui.base.BaseViewHolder;

import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-17 15:44
 * 描述：
 */

public class AlbumAdapter extends BaseAdapter<Album, AlbumAdapter.ViewHolder> {

    private int selectPosition;

    public AlbumAdapter(List<Album> list) {
        super(list);
    }

    private OnItemSelectListener<Album> onItemSelectListener;


    public void setOnItemSelectListener(OnItemSelectListener<Album> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setSelect(int position) {

        getList().get(selectPosition).setSelected(false);
        refreshItem(selectPosition);

        this.selectPosition = position;
        getList().get(position).setSelected(true);
        refreshItem(selectPosition);
    }

    public Album getSelect() {
        return getList().get(selectPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Image image = list.get(position).getImage();
        if (image != null) {
            PickerUI.getInstance().getImageLoader().loadThumbnails(holder.album, image.getThumbnailPath());
        }
        holder.ib.setSelected(list.get(position).isSelected());
        holder.name.setText(list.get(position).getBucketName());
        holder.count.setText("共" + list.get(position).getCount() + "张");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_album_default, parent, false));
    }

    class ViewHolder extends BaseViewHolder {
        ImageView album;
        TextView name;
        TextView count;
        ImageButton ib;


        ViewHolder(View itemView) {
            super(itemView);
            album = (ImageView) itemView.findViewById(R.id.iv_album);
            name = (TextView) itemView.findViewById(R.id.tv_album);
            count = (TextView) itemView.findViewById(R.id.tv_album_count);
            ib = (ImageButton) itemView.findViewById(R.id.ib);
            if (onItemSelectListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemSelectListener.itemClick(getAdapterPosition(), list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
}
