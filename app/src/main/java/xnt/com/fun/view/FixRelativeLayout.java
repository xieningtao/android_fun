package xnt.com.fun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sf.loglib.L;

/**
 * Created by g8876 on 2018/3/22.
 */

public class FixRelativeLayout extends RelativeLayout {
    public static final int KEYBOARD_HEIGH = 200;
    private final String TAG ="FixFrameLayout";
    private int mMaxHeight = 0;

    private int mMargin = 0;

    public FixRelativeLayout(Context context) {
        this(context, null);
    }

    public FixRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FixRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(mMaxHeight == 0){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mMaxHeight = getMeasuredHeight();
        }else {
            if(mMaxHeight < originHeight){
                mMaxHeight = originHeight;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight - mMargin, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
        L.info(TAG, "method->onMeasure height: " + (mMaxHeight - mMargin) + " measureHeight: " + getMeasuredHeight()
                + " originHeight: " + originHeight);
    }

    public void setCustomMargin(int margin) {
        this.mMargin = margin;
    }
}
