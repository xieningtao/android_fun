package xnt.com.fun;

import com.basesmartframe.baseapp.BaseApp;
import com.sflib.reflection.core.ThreadHelp;

import cn.bmob.v3.Bmob;
import xnt.com.fun.config.NYBMobConfig;


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
//        SFBaiduLocationManager.getInstance().init(getApplicationContext());
//        SFBaiduLocationManager.getInstance().requestLocate();
        ThreadHelp.initThread(this);
        initBmob();
    }
    private void initShare(){

    }

    private void initBmob(){
        //第一：默认初始化
        Bmob.initialize(this, NYBMobConfig.BMOB_APP_ID);
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        //Bmob.initialize(this, "Your Application ID","bmob");

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
    }
}