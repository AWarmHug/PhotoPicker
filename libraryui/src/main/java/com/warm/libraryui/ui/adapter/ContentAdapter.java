package com.warm.libraryui.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.warm.library.find.bean.ImageBean;
import com.warm.libraryui.R;
import com.warm.libraryui.DataManager;
import com.warm.libraryui.ui.base.BaseAdapter;
import com.warm.libraryui.ui.base.BaseViewHolder;
import com.warm.libraryui.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：warm
 * 时间：2017-10-17 14:13
 * 描述：
 */

public class ContentAdapter extends BaseAdapter<ImageBean, ContentAdapter.ViewHolder> {

    private static final int HEADER = -1;
    private static final String TAG = "ContentAdapter";

    private List<ImageBean> selectedImages;

    private boolean needHeader;
    private boolean more;

    private OnItemSelectListener<ImageBean> onItemSelectListener;


    public void setNeedHeader(boolean needHeader) {
        this.needHeader = needHeader;

    }

    public void setOnItemSelectListener(OnItemSelectListener<ImageBean> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public List<ImageBean> getSelectedImages() {
        return selectedImages;
    }

    public ContentAdapter(List<ImageBean> list) {
        this(list, false);
    }

    public ContentAdapter(List<ImageBean> list, boolean more) {
        super(list);
        selectedImages = new ArrayList<>();
        this.more = more;
    }


    public void setSelects(List<ImageBean> imageBeen) {
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
        if (viewType==HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recy_header, parent, false);
//            int height = parent.getMeasuredHeight() / 4;
//            view.setMinimumHeight(height);
            return new ViewHolder(view);
        }else {
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
            holder.iv.setImageResource( DataManager.getInstance().getConfig().getCameraIcon());
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.bgColor));
        } else {
            if (more) {
                holder.cb.setVisibility(View.VISIBLE);
                if (list.get(position - getHeaderSize()).isSelected()) {
                    holder.cb.setImageResource(DataManager.getInstance().getConfig().getSelectIcon()[0]);
                } else {
                    holder.cb.setImageResource(DataManager.getInstance().getConfig().getSelectIcon()[1]);
                }
            } else {
                holder.cb.setVisibility(View.GONE);
            }
            DataManager.getInstance().getImageLoader().loadThumbnails(holder.iv, "file://" + list.get(position - getHeaderSize()).getThumbnailPath());
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
