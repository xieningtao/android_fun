package xnt.com.fun;

import android.os.Bundle;

import com.basesmartframe.baseui.BaseActivity;

public class NYFragmentContainerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_activity);
        Utils.setActionBar(this);
        FragmentHelper fragmentHelper = new FragmentHelper(this);
        fragmentHelper.ensureFragment();
    }


}
