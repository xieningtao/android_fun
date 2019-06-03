package com.xnt.baselib.pagestatus.pagestatus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

public class DefaultPageViewStatus extends AbsPageViewStatus {
    public DefaultPageViewStatus(@NonNull Context context) {
        super(context);
    }

    public DefaultPageViewStatus(@NonNull Context context, int layoutRes) {
        super(context, layoutRes);
    }

    public DefaultPageViewStatus(@NonNull Context context, View contentView) {
        super(context, contentView);
    }
}
