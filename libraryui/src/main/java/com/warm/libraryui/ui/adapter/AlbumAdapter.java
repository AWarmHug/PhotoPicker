package com.warm.libraryui.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.warm.library.find.bean.AlbumBean;
import com.warm.libraryui.R;
import com.warm.libraryui.config.DataManager;
import com.warm.libraryui.base.BaseAdapter;
import com.warm.libraryui.base.BaseViewHolder;

import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-17 15:44
 * 描述：
 */

public class AlbumAdapter extends BaseAdapter<AlbumBean, AlbumAdapter.ViewHolder> {

    private int selectPosition;

    public AlbumAdapter(List<AlbumBean> list) {
        super(list);
    }

    private OnItemSelectListener<AlbumBean> onItemSelectListener;


    public void setOnItemSelectListener(OnItemSelectListener<AlbumBean> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setSelect(int position) {

        getList().get(selectPosition).setSelected(false);
        refreshItem(selectPosition);

        this.selectPosition = position;
        getList().get(position).setSelected(true);
        refreshItem(selectPosition);
    }

    public AlbumBean getSelect() {
        return getList().get(selectPosition);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DataManager.getInstance().getILoader().loadThumbnails(holder.album, list.get(position).getImage().getThumbnailPath());

        holder.select.setImageResource(list.get(position).isSelected() ? DataManager.getInstance().getConfig().getSelectIcon()[0] : DataManager.getInstance().getConfig().getSelectIcon()[1]);
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
