package com.warm.pickerui.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.warm.picker.find.entity.Image;
import com.warm.pickerui.config.PickerUI;
import com.warm.pickerui.R;
import com.warm.pickerui.ui.base.BaseAdapter;
import com.warm.pickerui.ui.base.BaseViewHolder;
import com.warm.pickerui.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-17 14:13
 * 描述：
 */

public class ContentAdapter extends BaseAdapter<Image, ContentAdapter.ViewHolder> {

    private static final int HEADER = -1;
    private static final String TAG = "photoPickerTAG";

    private List<Image> selectedImages;

    private boolean needHeader;
    private boolean more;

    private OnItemSelectListener<Image> onItemSelectListener;


    public void setNeedHeader(boolean needHeader) {
        this.needHeader = needHeader;

    }

    public void setOnItemSelectListener(OnItemSelectListener<Image> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public List<Image> getSelectedImages() {
        return selectedImages;
    }

    public ContentAdapter(List<Image> list) {
        this(list, false);
    }

    public ContentAdapter(List<Image> list, boolean more) {
        super(list);
        selectedImages = new ArrayList<>();
        this.more = more;
    }


    public void setSelects(List<Image> imageBeen) {
        selectedImages.clear();
        selectedImages.addAll(imageBeen);
    }

    public void checkedAdd(int position) {
        selectedImages.add(list.get(position));
    }

    public void checkedRemove(int position) {
        selectedImages.remove(list.get(position));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy_header, parent, false);
//            int height = parent.getMeasuredHeight() / 4;
//            view.setMinimumHeight(height);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy, parent, false);
//            int height = parent.getMeasuredHeight() / 4;
//            view.setMinimumHeight(height);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (getItemViewType(position) == HEADER) {
            holder.cb.setVisibility(View.GONE);
            holder.iv.setImageResource(PickerUI.getInstance().getConfig().getCameraIcon());
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.bgColor));
        } else {
            if (more) {
                holder.cb.setVisibility(View.VISIBLE);
                holder.cb.setSelected(list.get(position - getHeaderSize()).isSelected());
                holder.cb.setImageResource(PickerUI.getInstance().getConfig().getSelectDrawable());
            } else {
                holder.cb.setVisibility(View.GONE);
            }
            PickerUI.getInstance().getImageLoader().loadThumbnails(holder.iv, "file://" + list.get(position - getHeaderSize()).getThumbnailPath());
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setText(list.get(position-getHeaderSize()).getMimeType());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (needHeader) {
            if (position == 0) {
                return HEADER;
            } else {
                return super.getItemViewType(position);
            }
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        if (needHeader) {
            return super.getItemCount() + 1;
        } else {
            return super.getItemCount();
        }
    }

    @Override
    public int getHeaderSize() {
        return getItemCount() - getList().size();
    }

    class ViewHolder extends BaseViewHolder {
        FrameLayout frame;
        ImageView iv;
        ImageView cb;
        TextView type;

        ViewHolder(View itemView) {
            super(itemView);
            int screenHeight = ScreenUtils.getScreenHeight(itemView.getContext());
            int screenWidth = ScreenUtils.getScreenWidth(itemView.getContext());
            int width = 100;
            if (screenHeight != 0 && screenWidth != 0) {
                width = (screenWidth - itemView.getResources().getDimensionPixelOffset(R.dimen.grid_space) * 4) / 3;
            }
            frame = (FrameLayout) itemView.findViewById(R.id.item_frame);
            iv = (ImageView) itemView.findViewById(R.id.item_iv);
            cb = (ImageView) itemView.findViewById(R.id.item_cv);
            type = itemView.findViewById(R.id.item_type);
            frame.getLayoutParams().width = width;
            frame.getLayoutParams().height = width;

            if (onItemSelectListener != null) {
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContentAdapter.this.getItemViewType(getAdapterPosition()) == HEADER) {
                            onItemSelectListener.cameraClick();
                        } else {
                            onItemSelectListener.itemClick(getAdapterPosition() - ContentAdapter.this.getHeaderSize(), list.get(getAdapterPosition() - ContentAdapter.this.getHeaderSize()));
                        }
                    }
                });
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemSelectListener.itemSelect(getAdapterPosition() - ContentAdapter.this.getHeaderSize(), list.get(getAdapterPosition() - ContentAdapter.this.getHeaderSize()));
                    }
                });
            }

        }
    }

}
