/**
 * @(#)WXEntryActivity.java, 2014-5-30. 
 * 
 * Copyright 2014 netease, Inc. All rights reserved.
 * Netease PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package xnt.com.fun.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.sf.loglib.L;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import xnt.com.fun.NYShareHelper;

/**
 * @author xltu
 */
//this is for umeng share
//public class WXEntryActivity extends WXCallbackActivity  {
//
//}

//this is userful for  wx lib platform
public class WXEntryActivity extends WXCallbackActivity  {
    private final String TAG = getClass().getName();
    private IWXAPI api;
//    private final String config="umeng";
//    private final String WX_UMENG="umeng";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(!WX_UMENG.equals(config)) {
//            api = WXAPIFactory.createWXAPI(this, SFManifestUtil.getMetaValue(this, ShareConstant.WEI_XIN_APP_ID), false);
//            api.registerApp(SFManifestUtil.getMetaValue(this, ShareConstant.WEI_XIN_APP_ID));
//            api.handleIntent(getIntent(), this);
//        }
        L.info(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        NYShareHelper.dismissDialog();
    }

//    @Override
//    public void onReq(BaseReq req) {
//        L.info(TAG, "onReq req type: " + req.getType()+" openId: "+req.openId);
//    }
//
//
//    @Override
//    public void onResp(BaseResp resp) {
//        L.info(TAG, "onResp resp type: " + resp.getType()+" openId: "+resp.openId+" errorCode: "+resp.errCode+" errorStr: "+resp.errStr);
//    }
}
