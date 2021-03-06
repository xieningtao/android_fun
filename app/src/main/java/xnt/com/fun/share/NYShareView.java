package xnt.com.fun.share;

import android.content.Context;
import android.util.AttributeSet;

import com.sflib.umenglib.share.sharecore.XBaseShareView;
import com.sflib.umenglib.share.shareitem.PengYouQuanBaseShareItem;
import com.sflib.umenglib.share.shareitem.SinaBaseShareItem;
import com.sflib.umenglib.share.shareitem.WeiXinBaseShareItem;
import com.sflib.umenglib.share.shareitem.XBaseShareItem;
import com.umeng.socialize.ShareAction;

import java.util.ArrayList;

/**
 * Created by mac on 2018/6/2.
 */

public class NYShareView extends XBaseShareView {

    private ShareAction mShareAction;

    public NYShareView(Context context) {
        super(context);
    }

    public NYShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setShareContent(ShareAction shareAction) {
        this.mShareAction = shareAction;
    }

    private ArrayList<XBaseShareItem> creatShareItems() {
        ArrayList<XBaseShareItem> shareItems = new ArrayList();
        WeiXinBaseShareItem weiXinShareItem = new WeiXinBaseShareItem(this.getContext(), this.mShareAction);
        shareItems.add(weiXinShareItem);
        PengYouQuanBaseShareItem pengYouQuanShareItem = new PengYouQuanBaseShareItem(this.getContext(), this.mShareAction);
        shareItems.add(pengYouQuanShareItem);
        SinaBaseShareItem sinaShareItem = new SinaBaseShareItem(getContext(), this.mShareAction);
        shareItems.add(sinaShareItem);
        return shareItems;
    }

    public ArrayList<XBaseShareItem> getShareItems() {
        return this.creatShareItems();
    }
}
