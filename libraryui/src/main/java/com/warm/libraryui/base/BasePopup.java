package com.warm.libraryui.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.PopupWindow;


/**
 * 作者: 51hs_android
 * 时间: 2017/3/2
 * 简介:
 */

public class BasePopup extends PopupWindow {
    private static final String TAG = "MyPopouWindow";

    private ViewGroup mParent;
    private View mGrey;


    public BasePopup(Context context) {
        super(context);
        set(context);

    }

    public BasePopup(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        set(contentView.getContext());
    }

    public BasePopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        set(context);

    }




    private void set(Context context) {
        setFocusable(true);
        setOutsideTouchable(true);
        //设置popupwindow 背景颜色
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(android.R.style.Animation_Dialog);
        addBackGround(context);
    }

    public void addBackGround(Context context) {
        mGrey = new View(context);
        mGrey.setBackgroundColor(Color.BLACK);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mGrey.setLayoutParams(lp);
    }



    public BasePopup(View contentView, int width, int height) {
        super(contentView, width, height);
        set(contentView.getContext());
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        mParent = findSuitableParent(parent);
        mParent.addView(mGrey);
        anim(0f, 0.5f, 300);
    }




    /**
     * @param anchor
     * @param viewGroup 需要变暗的ViewGroup,如果传null,就代表全屏变暗
     * @param xoff
     * @param yoff
     * @param gravity
     */
    public void showAsDropDown(View anchor, ViewGroup viewGroup, int xoff, int yoff, int gravity) {
        this.showAsDropDown(anchor, xoff, yoff, gravity);
        if (viewGroup == null) {
            mParent = findSuitableParent(anchor);
        } else {
            mParent = viewGroup;
        }
        mParent.addView(mGrey);
        anim(0f, 0.5f, 300);
    }


    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        anim(0.5f, 0f, 300);
        if (mParent!=null&&mGrey != null) {
            mParent.removeView(mGrey);
        }
    }


    private void anim(float start, float end, int time) {
        Log.d(TAG, "anim: ");
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGrey.setAlpha((float) animation.getAnimatedValue());

            }
        });
        animator.start();
    }

    public interface OnViewClick {
        void viewClick(View v);
    }

    public interface OnClickSure {

        void click(View view, int position);

    }


    private ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

}
