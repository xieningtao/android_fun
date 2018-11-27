package xnt.com.fun.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import xnt.com.fun.R;

public class NYProgressDialog {

    public static Dialog getProgressDialog(Activity activity,String content){
        View progressView = LayoutInflater.from(activity).inflate(R.layout.ny_progress_dialog,null);
        TextView loadTv = progressView.findViewById(R.id.load_tv);
        loadTv.setText(content);
        Dialog dialog = DialogHelper.getNoTitleDialog(activity,progressView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}
