package xnt.com.fun;

import com.basesmartframe.baseapp.BaseApp;
import com.sf.baidulib.SFBaiduLocationManager;
import com.sflib.reflection.core.ThreadHelp;


public class SFApp extends BaseApp {
    public static final String APP_ID = "57f9edc887d4a7e337b8c231";
//    public static final String APP_ID_KEY = "MmNsUDJONjlNc2xwNzEtbVY3RE5KUQ";
    public static final String APP_ID_KEY = "WHB0a1QzUXZwNDZJMXFYYjNpbnJxZw";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        startModule();
    }

    private void startModule() {

    }

    private void init() {
        SFBaiduLocationManager.getInstance().init(getApplicationContext());
        SFBaiduLocationManager.getInstance().requestLocate();
        ThreadHelp.initThread(this);
    }
}