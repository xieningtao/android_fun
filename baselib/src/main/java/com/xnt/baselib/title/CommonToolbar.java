package com.xnt.baselib.title;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xnt.baselib.R;

import java.lang.reflect.Field;

/**
 * Created by yuhengye g10475 on 2018/6/11.
 **/

public class CommonToolbar extends Toolbar {

    int mElevation;
    TextView mTitleTextView;

    public CommonToolbar(Context context) {
        this(context, null);
    }

    public CommonToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.appcompat.R.attr.toolbarStyle);
    }

    public CommonToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void init() {

        setNavigationIcon(R.drawable.base_action_bar_back_new);
        setBackgroundColor(getResources().getColor(R.color.widget_toolbar_color));
        setTitleTextAppearance(getContext(), R.style.widget_toolbar_title_style);
        mElevation = getResources().getDimensionPixelSize(R.dimen.widget_toolbar_elevation);
        setElevation(true);
        setContentInsetStartWithNavigation(0);

    }

    public void setElevation(boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(show ? mElevation : 0);
        }
    }


    public TextView getTitleTextView() {
        if (mTitleTextView != null) {
            return mTitleTextView;
        }

        try {

            Field field = Toolbar.class.getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            mTitleTextView = (TextView) field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTitleTextView;
    }

    /**
     * 改变标题栏右侧按钮或者文字颜色
     * @param color
     */
    public void updateActionMenuViewColor(@ColorInt int color) {
        ColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child instanceof ActionMenuView) {
                ActionMenuView actionMenuView = (ActionMenuView) child;
                for (int j = 0; j < actionMenuView.getChildCount(); j++) {
                    View actionMenuChildView = actionMenuView.getChildAt(j);

                    if (actionMenuChildView instanceof TextView) {
                        TextView textView = (TextView) actionMenuChildView;
                        Drawable drawable = textView.getCompoundDrawables()[0];
                        if (drawable == null) {
                            textView.setTextColor(color);
                        } else if (!porterDuffColorFilter.equals(DrawableCompat.getColorFilter(drawable))) {
                            drawable = drawable.mutate();
                            drawable.setColorFilter(porterDuffColorFilter);
                            textView.setCompoundDrawables(drawable, null, null, null);
                        }
                    } else if (actionMenuChildView instanceof ImageView) {
                        ImageView imageView = (ImageView) actionMenuChildView;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                                !porterDuffColorFilter.equals(imageView.getColorFilter())) {
                            imageView.setColorFilter(porterDuffColorFilter);
                        }
                    }
                }
            }
        }
    }

    /**
     * 改变标题栏文字颜色
     * @param colorStateId
     */
    public void updateActionMenuTextViewColorState(int colorStateId) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof ActionMenuView) {
                ActionMenuView actionMenuView = (ActionMenuView) child;
                for (int j = 0; j < actionMenuView.getChildCount(); j++) {
                    View actionMenuChildView = actionMenuView.getChildAt(j);
                    if (actionMenuChildView instanceof TextView) {
                        TextView textView = (TextView) actionMenuChildView;
                        textView.setTextColor(getResources().getColorStateList(colorStateId));
                    }
                }
            }
        }
    }

}
