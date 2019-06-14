package xnt.com.fun.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class ExViewPager extends ViewPager {
    private ExViewPagerTouchListener mOnTouchListener;

    public ExViewPager(@NonNull Context context) {
        this(context,null);
    }

    public ExViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExViewPagerTouchListener getOnTouchViewPagerListener() {
        return mOnTouchListener;
    }

    public void setOnTouchViewPagerListener(ExViewPagerTouchListener onTouchListener) {
        this.mOnTouchListener = onTouchListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mOnTouchListener != null){
            mOnTouchListener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public  interface ExViewPagerTouchListener{
        void onTouch(MotionEvent event);
    }
}
