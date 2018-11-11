package xnt.com.fun;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;

import com.sf.loglib.L;

public class FragmentUtils {
    public static boolean showViewWithSlideBottom(Activity activity,
                                                  int containerId,
                                                  Class<? extends Fragment> instance,
                                                  String tag,
                                                  Bundle bundle) {
        if(activity==null  || activity.isFinishing()){
            return false;
        }
        if(instance == null || TextUtils.isEmpty(tag)){
            return false;
        }
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {

            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            try {
                fragment = instance.newInstance();
                fragment.setArguments(bundle);
                transaction.add(containerId, fragment, tag).commit();
            } catch (InstantiationException e) {
                L.error("EXCEPTION","msg: "+e.getMessage());
            } catch (IllegalAccessException e) {
                L.error("EXCEPTION","msg: "+e.getMessage());
            }
        } else {
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.show(fragment).commit();
        }
        return true;
    }

    public static boolean hideViewWithSlideBottom(Activity activity, String tag) {
        if (activity == null
                || activity.isFinishing() || TextUtils.isEmpty(tag)) {
            return false;
        }

        Fragment fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (fragment != null && fragment.isVisible()) {
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.hide(fragment).commit();
            return true;
        }
        return false;
    }
    public static boolean removeViewWithSlideBottom(Activity activity, String tag) {
        if (activity == null
                || activity.isFinishing() || TextUtils.isEmpty(tag)) {
            return false;
        }

        Fragment fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (fragment != null && fragment.isVisible()) {
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.remove(fragment).commit();
            return true;
        }
        return false;
    }
}
