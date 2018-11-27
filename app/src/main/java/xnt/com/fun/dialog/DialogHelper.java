package xnt.com.fun.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import xnt.com.fun.R;

public class DialogHelper {

    public static Dialog getNoTitleDialog(Context context, View view){
        final Dialog dialog = new Dialog(context, R.style.operationDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    public static Dialog getNoTitleDialog(Context context, View view,int style){
        final Dialog dialog = new Dialog(context, style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
