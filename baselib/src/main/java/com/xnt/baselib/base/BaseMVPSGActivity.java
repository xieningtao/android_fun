package com.xnt.baselib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xnt.baselib.mvp.AbsPresenter;
import com.xnt.baselib.mvp.IView;

abstract public class BaseMVPSGActivity<V extends IView,P extends AbsPresenter<V>> extends BaseCommonSGActivity {

    protected AbsPresenter<V> mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(null!=mPresenter){
            mPresenter.onAttach(getIView());
        }
    }

    protected abstract V getIView();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(null!=mPresenter){
            mPresenter.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        if(null!=mPresenter){
            mPresenter.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(null!=mPresenter){
            mPresenter.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDetachedFromWindow() {
        if(null!=mPresenter){
            mPresenter.onDetach();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        if(null!=mPresenter){
            mPresenter.onDetach();
            mPresenter.onDestroy();
            mPresenter = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(null!=mPresenter){
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(null!=mPresenter){
            mPresenter.onResume();
        }
        super.onResume();
    }

    protected abstract P createPresenter();

}
