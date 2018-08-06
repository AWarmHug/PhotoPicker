package com.warm.pickerui.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.warm.picker.find.entity.Album;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        PickerUI.getInstance().getImageLoader().loadThumbnails(holder.album, list.get(position).getImage().getThumbnailPath());
        holder.select.setSelected(list.get(position).isSelected());
        holder.select.setImageResource(PickerUI.getInstance().getConfig().getSelectDrawable());
        holder.name.setText(list.get(position).getBucketName());
        holder.count.setText("共" + list.get(position).getCount() + "张");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy_album, parent, false));
    }

    class ViewHolder extends BaseViewHolder {
        ImageView album;
        TextView name;
        TextView count;
        ImageView select;


        public ViewHolder(View itemView) {
            super(itemView);
            album = (ImageView) itemView.findViewById(R.id.iv_album);
            name = (TextView) itemView.findViewById(R.id.tv_album);
            count = (TextView) itemView.findViewById(R.id.tv_album_count);
            select = (ImageView) itemView.findViewById(R.id.cb);
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
