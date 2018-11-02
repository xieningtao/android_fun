package xnt.com.fun;

import java.util.ArrayList;
import java.util.List;

import xnt.com.fun.bean.Beauty;

public class BeautyModel {

    private static Object mLock = new Object();
    private static BeautyModel sInstance;
    private List<Beauty> mBeauties = new ArrayList<>();
    private List<OnDataChangeListener> mChangeListeners= new ArrayList<>();

    private BeautyModel() {

    }

    public static BeautyModel getInstance() {
        if (sInstance == null) {
            synchronized (mLock) {
                if (sInstance == null) {
                    sInstance = new BeautyModel();
                }
            }
        }
        return sInstance;
    }

    public void registerListener(OnDataChangeListener listener){
        mChangeListeners.add(listener);
    }

    public void unregisterListener(OnDataChangeListener listener){
        mChangeListeners.remove(listener);
    }

    private void doNotify(){
        for (OnDataChangeListener listener:mChangeListeners){
            listener.onDataChange();
        }
    }

    public void addBeauty(int position, Beauty beauty) {
        if (position >= 0 && position < mBeauties.size()) {
            mBeauties.add(position, beauty);
            doNotify();
        } else {
            addBeauty(beauty);
        }

    }

    public void addBeauties(int position, List<Beauty> beauties) {
        if (position >= 0 && position < mBeauties.size()) {
            mBeauties.addAll(position, beauties);
            doNotify();
        } else {
            addBeauties(beauties);
        }
    }

    public void addBeauties(List<Beauty> beauties) {
        mBeauties.addAll(beauties);
        doNotify();
    }

    public void addBeauty(Beauty beauty) {
        mBeauties.add(beauty);
        doNotify();
    }

    public Beauty getBeauty(int pos) {
        return mBeauties.get(pos);
    }

    public int getBeautySize() {
        return mBeauties.size();
    }

    public List<Beauty> getBeauties() {
        return mBeauties;
    }

    public void clear() {
        mBeauties.clear();
    }

    public static interface OnDataChangeListener {
        void onDataChange();
    }
}
