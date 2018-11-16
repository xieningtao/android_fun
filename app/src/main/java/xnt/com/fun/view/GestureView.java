package xnt.com.fun.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class GestureView extends FrameLayout {
    private GestureDetector mDetector ;
    private GestureDetector.SimpleOnGestureListener mGestureListener;
    public GestureView(@NonNull Context context) {
        this(context,null);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnGestureListener(GestureDetector.SimpleOnGestureListener gestureListener){
        mGestureListener = gestureListener;
    }

    private void init(){
        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(mGestureListener != null){
                    return mGestureListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if(mGestureListener != null){
                    return mGestureListener.onDown(e);
                }
                return super.onDown(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(mGestureListener != null){
                    return mGestureListener.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(mGestureListener != null){
                    return mGestureListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

}
