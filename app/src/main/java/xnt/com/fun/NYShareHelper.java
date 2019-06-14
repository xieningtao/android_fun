package xnt.com.fun;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.sf.utils.baseutil.SFToast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import xnt.com.fun.dialog.NYProgressDialog;

public class NYShareHelper {
    private static Dialog progressDialog;

    public static ShareAction getShareAction(Activity activity, String content, String imgUrl) {
        if (progressDialog == null) {
            progressDialog = NYProgressDialog.getProgressDialog(activity, activity.getString(R.string.sharing));
        }
        UMImage umImage = new UMImage(activity, imgUrl);
        if (TextUtils.isEmpty(content)) {
            content = "M拍，找寻时尚的你";
        }
        return createShareAction(activity, content, umImage);
    }

    private static ShareAction createShareAction(Activity activity, String content, UMImage umImage) {
        return new ShareAction(activity).withText(content).withExtra(umImage).setCallback(new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                progressDialog.dismiss();
                SFToast.showToast(R.string.share_success);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                progressDialog.dismiss();
                SFToast.showToast(R.string.share_fail);
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                progressDialog.dismiss();
                SFToast.showToast(R.string.share_cancel);
            }
        });
    }

    public static ShareAction getShareAction(Activity activity, String content, Bitmap bitmap) {
        if (progressDialog == null) {
            progressDialog = NYProgressDialog.getProgressDialog(activity, activity.getString(R.string.sharing));
        }
        UMImage umImage = new UMImage(activity, bitmap);
        if (TextUtils.isEmpty(content)) {
            content = "M拍，找寻时尚的你";
        }
        return createShareAction(activity, content, umImage);
    }

    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static Bitmap resizeDownBySideLength(
            Drawable drawable, int maxLength) {
        int srcWidth = drawable.getIntrinsicWidth();
        int srcHeight = drawable.getIntrinsicHeight();
        float scale = Math.min(
                (float) maxLength / srcWidth, (float) maxLength / srcHeight);
        return resizeBitmapByScale(drawable, scale);
    }

    public static Bitmap resizeBitmapByScale(
            Drawable drawable, float scale) {
        int width = Math.round(drawable.getIntrinsicWidth() * scale);
        int height = Math.round(drawable.getIntrinsicHeight() * scale);
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return target;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        return resizeDownBySideLength(drawable, 1024 * 1024);
    }
}
