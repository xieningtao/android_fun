package xnt.com.fun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sf.loglib.L;
import com.sf.utils.baseutil.UnitHelp;

public class FixLinearLayout extends LinearLayout {

    private final String TAG ="FixLinearLayout";
    private int mMaxHeight = 0;

    public FixLinearLayout(Context context) {
        this(context,null);
    }

    public FixLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public FixLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            }else {
                if (Math.abs(mMaxHeight - originHeight) > UnitHelp.dip2px(getContext(), 60)) {
                    //键盘弹起和隐藏,虚拟navigation弹起和隐藏需要重新布局
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
                }
            }
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        L.info(TAG,"method->onMeasure height: "+mMaxHeight +" measureHeight: "+getMeasuredHeight()
                + " originHeight: "+originHeight);
    }
}
