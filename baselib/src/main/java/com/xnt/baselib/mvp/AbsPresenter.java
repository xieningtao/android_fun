package com.xnt.baselib.mvp;

import android.content.Intent;
import android.os.Bundle;


import java.lang.reflect.Array;
import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;


public abstract class AbsPresenter<V extends IView> {
    protected ArrayList<Subscription> mSubscriptions = new ArrayList<Subscription>();

    protected V mView;

    public AbsPresenter() {

    }

    public void onAttach(V view){
        mView = view;
    }

    public void onDetach(){
        mView = null;
    }

    public void onDestroy(){
        if(null != mSubscriptions){
            for (Subscription subscription : mSubscriptions){
                if (subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                    subscription = null;
                }
            }
            mSubscriptions.clear();
        }
    }

    public void onCreate(){}

    public void onSaveInstanceState(Bundle outState){}

    public void onStart(){}

    public void onResume(){}

    public void onReStart(){}

    public void onPause(){}

    public void onStop(){}

    public void onActivityResult(int requestCode, int resultCode, Intent data){}

    public void cancelTask(){}

    public void initData(){}




}
