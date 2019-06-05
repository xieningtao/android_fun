package com.xnt.baselib.commonevent;

public class NetworkChangeEvent {
    public final boolean mIsConnected;
    public final boolean mIsWifi;

    public NetworkChangeEvent(boolean mIsConnected, boolean mIsWifi) {
        this.mIsConnected = mIsConnected;
        this.mIsWifi = mIsWifi;
    }
}
