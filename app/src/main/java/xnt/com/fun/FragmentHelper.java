package xnt.com.fun;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

public class FragmentHelper {
    public static final String SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM = "SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM";

    private static final String FRAGMENT_NAME = "fragment_name";

    private static final String FRAGMENT_TAG = "fragment_tag";

    private static final String FRAGMENT_ARG = "fragment_argu";

    private static final String EXTRA = "extra";

    public static final String EXTRA_PARAM_LAYOUT = "layout";

    public static final String EXTRA_PARAM_CONTAINER = "container";

    private Activity mActivity;
    private Fragment mFragment;

    public static Intent getStartIntent(@NonNull Context context, Class<? extends Fragment> fragmentClass, Bundle fragmentArgu, Bundle extra,
                                        Class clazz) {
        Intent intent = new Intent(context, clazz);
        putSingleBundle(intent, fragmentClass.getName(), fragmentClass.getSimpleName(), fragmentArgu, extra);
        return intent;
    }

    public static void putSingleBundle(Intent intent, String fragmentName, String fragmentTag, Bundle fragmentArgu, Bundle extra) {
        Bundle b = new Bundle();
        b.putString(FRAGMENT_NAME, fragmentName);
        b.putString(FRAGMENT_TAG, fragmentTag);
        b.putBundle(FRAGMENT_ARG, fragmentArgu);
        b.putInt(EXTRA_PARAM_CONTAINER, R.id.fragment_container_fl);
        if (extra != null) {
            b.putBundle(EXTRA, extra);
        }
        intent.putExtra(SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM, b);
    }

    public FragmentHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * 初始化fragment
     */
    void ensureFragment() {
        Bundle param = mActivity.getIntent().getBundleExtra(SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM);
        if (param == null) {
            return;
        }

        ensureFragmentInternal(param);
    }

    /**
     * 初始化fragment
     *
     * @param param
     */
    private void ensureFragmentInternal(Bundle param) {
        Bundle extraParam = param.getBundle(EXTRA);

        // 设置activity layout
        int layoutid = extraParam == null ? 0 : extraParam.getInt(EXTRA_PARAM_LAYOUT, 0);
        if (layoutid != 0) {
            mActivity.setContentView(layoutid);
        }
        String fragmentTag = param.getString(FRAGMENT_TAG);
        mFragment = mActivity.getFragmentManager().findFragmentByTag(fragmentTag);
        if (mFragment == null) {
            // 初始化fragment
            String fragmentName = param.getString(FRAGMENT_NAME);
            Bundle argu = param.getBundle(FRAGMENT_ARG);

            // 设置fragment container id
            int containerId = layoutid == 0 || extraParam == null ? 0 : extraParam.getInt(EXTRA_PARAM_CONTAINER, 0);
            if (containerId != 0) {
                mFragment = addFragmentByTag(containerId, fragmentName, fragmentTag, argu);
            } else {
                mFragment = addFragmentByTag(android.R.id.content, fragmentName, fragmentTag, argu);
            }
        }
    }

    /**
     * 增加fragment
     *
     * @param container
     * @param clazz
     * @param tag
     * @param argument
     * @return
     */
    private Fragment addFragmentByTag(int container, String clazz, String tag, Bundle argument) {
        FragmentManager fm = mActivity.getFragmentManager();

        Fragment f = fm.findFragmentByTag(tag);
        if (f == null) {
            FragmentTransaction ft = fm.beginTransaction();
            f = Fragment.instantiate(mActivity, clazz, argument);
            if (container == 0) {
                ft.add(f, tag);
            } else {
                ft.add(container, f, tag);
            }

            ft.commit();
        } else if (f.isDetached()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.attach(f);
            ft.commit();
        }

        return f;
    }
}
